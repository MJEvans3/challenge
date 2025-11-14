# Database Schema Design

This document describes the database schema design to support dynamic workflow configuration and execution.

## Entity Relationship Diagram

```
┌─────────────────────────┐
│      workflows          │
│─────────────────────────│
│ id (PK)                 │
│ name                    │
│ description             │
│ is_active               │
│ created_at              │
│ updated_at              │
└────────────┬────────────┘
             │
             │ 1:N
             │
┌────────────▼────────────┐
│   workflow_rules        │
│─────────────────────────│
│ id (PK)                 │
│ workflow_id (FK)        │
│ priority                │
│ is_active               │
│ description             │
│ created_at              │
└────────┬────────────────┘
         │
         │ 1:N
         ├──────────────────────────────┐
         │                              │
┌────────▼────────────┐    ┌───────────▼──────────┐
│  rule_conditions    │    │   rule_actions       │
│─────────────────────│    │──────────────────────│
│ id (PK)             │    │ id (PK)              │
│ rule_id (FK)        │    │ rule_id (FK)         │
│ condition_type      │    │ approver_role        │
│ field_name          │    │ notification_channel │
│ operator            │    │ message_template     │
│ value               │    │ created_at           │
│ created_at          │    └──────────────────────┘
└─────────────────────┘

┌──────────────────────────┐
│  workflow_executions     │
│──────────────────────────│
│ id (PK)                  │
│ workflow_id (FK)         │
│ rule_id (FK)             │
│ invoice_data (JSONB)     │
│ action_result            │
│ executed_at              │
└──────────────────────────┘
```

## Table Definitions

### 1. workflows
Stores workflow definitions. Multiple workflows can coexist.

```sql
CREATE TABLE workflows (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_workflows_active ON workflows(is_active);
```

**Example:**
```sql
INSERT INTO workflows (name, description) VALUES 
('Invoice Approval Workflow', 'Standard invoice approval process');
```

---

### 2. workflow_rules
Stores individual rules within a workflow. Rules are evaluated in priority order.

```sql
CREATE TABLE workflow_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workflow_id UUID NOT NULL REFERENCES workflows(id) ON DELETE CASCADE,
    priority INTEGER NOT NULL,
    is_active BOOLEAN DEFAULT true,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_workflow_priority UNIQUE (workflow_id, priority)
);

CREATE INDEX idx_workflow_rules_priority ON workflow_rules(workflow_id, priority);
CREATE INDEX idx_workflow_rules_active ON workflow_rules(is_active);
```

**Example:**
```sql
INSERT INTO workflow_rules (workflow_id, priority, description) VALUES 
('workflow-uuid', 1, 'High value marketing invoices go to CMO');
```

---

### 3. rule_conditions
Stores conditions for each rule. All conditions in a rule are ANDed together.

```sql
CREATE TABLE rule_conditions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rule_id UUID NOT NULL REFERENCES workflow_rules(id) ON DELETE CASCADE,
    condition_type VARCHAR(50) NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    operator VARCHAR(20) NOT NULL,
    value VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_rule_conditions_rule ON rule_conditions(rule_id);
```

**Condition Types:**
- `AMOUNT_COMPARISON`: Compares invoice amount
- `DEPARTMENT_EQUALS`: Checks department equality
- `MANAGER_APPROVAL_REQUIRED`: Checks manager approval flag

**Operators:**
- `GREATER_THAN`, `GREATER_THAN_OR_EQUAL`
- `LESS_THAN`, `LESS_THAN_OR_EQUAL`
- `EQUAL`, `NOT_EQUAL`

**Examples:**
```sql
-- Amount > 10000
INSERT INTO rule_conditions (rule_id, condition_type, field_name, operator, value) VALUES 
('rule-uuid-1', 'AMOUNT_COMPARISON', 'amount', 'GREATER_THAN', '10000');

-- Department = 'marketing'
INSERT INTO rule_conditions (rule_id, condition_type, field_name, operator, value) VALUES 
('rule-uuid-1', 'DEPARTMENT_EQUALS', 'department', 'EQUAL', 'marketing');

-- Requires manager approval = true
INSERT INTO rule_conditions (rule_id, condition_type, field_name, operator, value) VALUES 
('rule-uuid-2', 'MANAGER_APPROVAL_REQUIRED', 'requiresManagerApproval', 'EQUAL', 'true');
```

---

### 4. rule_actions
Stores the action to take when a rule matches.

```sql
CREATE TABLE rule_actions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rule_id UUID NOT NULL REFERENCES workflow_rules(id) ON DELETE CASCADE,
    approver_role VARCHAR(100) NOT NULL,
    notification_channel VARCHAR(50) NOT NULL,
    message_template TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_rule_action UNIQUE (rule_id)
);

CREATE INDEX idx_rule_actions_rule ON rule_actions(rule_id);
```

**Approver Roles:**
- `FINANCE_TEAM`, `FINANCE_MANAGER`, `CFO`, `CMO`

**Notification Channels:**
- `SLACK`, `EMAIL`

**Example:**
```sql
INSERT INTO rule_actions (rule_id, approver_role, notification_channel, message_template) VALUES 
('rule-uuid-1', 'CMO', 'EMAIL', 'High value marketing invoice requires CMO approval');
```

---

### 5. workflow_executions (Audit Log)
Records all workflow evaluations for audit and analytics.

```sql
CREATE TABLE workflow_executions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workflow_id UUID NOT NULL REFERENCES workflows(id),
    rule_id UUID REFERENCES workflow_rules(id),
    invoice_data JSONB NOT NULL,
    action_result TEXT,
    executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_workflow_executions_workflow ON workflow_executions(workflow_id);
CREATE INDEX idx_workflow_executions_executed_at ON workflow_executions(executed_at DESC);
CREATE INDEX idx_workflow_executions_invoice_data ON workflow_executions USING GIN (invoice_data);
```

**Example:**
```sql
INSERT INTO workflow_executions (workflow_id, rule_id, invoice_data, action_result) VALUES 
('workflow-uuid', 'rule-uuid', 
 '{"amount": 15000, "department": "marketing", "requiresManagerApproval": false}',
 'Sent approval to CMO via EMAIL');
```

---

## Complete Workflow Example

### Creating a Rule: "Amount > 10000 AND Department = marketing → CMO via Email"

```sql
-- 1. Create the workflow
INSERT INTO workflows (id, name, description) VALUES 
('wf-001', 'Invoice Approval', 'Standard approval workflow');

-- 2. Create the rule
INSERT INTO workflow_rules (id, workflow_id, priority, description) VALUES 
('rule-001', 'wf-001', 1, 'High value marketing to CMO');

-- 3. Add conditions (both must be satisfied)
INSERT INTO rule_conditions (rule_id, condition_type, field_name, operator, value) VALUES 
('rule-001', 'AMOUNT_COMPARISON', 'amount', 'GREATER_THAN', '10000'),
('rule-001', 'DEPARTMENT_EQUALS', 'department', 'EQUAL', 'marketing');

-- 4. Add action
INSERT INTO rule_actions (rule_id, approver_role, notification_channel, message_template) VALUES 
('rule-001', 'CMO', 'EMAIL', 'High value marketing invoice requires CMO approval');
```

---

## Query Examples

### Get all rules for a workflow (in priority order)
```sql
SELECT r.*, 
       array_agg(DISTINCT rc.condition_type) as conditions,
       ra.approver_role,
       ra.notification_channel
FROM workflow_rules r
LEFT JOIN rule_conditions rc ON rc.rule_id = r.id
LEFT JOIN rule_actions ra ON ra.rule_id = r.id
WHERE r.workflow_id = 'wf-001' AND r.is_active = true
GROUP BY r.id, ra.id
ORDER BY r.priority ASC;
```

### Find matching rule for an invoice
```sql
WITH invoice_data AS (
    SELECT 15000 as amount, 'marketing' as department, false as requires_manager_approval
)
SELECT r.*, ra.*
FROM workflow_rules r
JOIN rule_actions ra ON ra.rule_id = r.id
WHERE r.workflow_id = 'wf-001'
  AND r.is_active = true
  -- Rule matching logic would be in application code
ORDER BY r.priority ASC
LIMIT 1;
```

### Audit: Get execution history for past 7 days
```sql
SELECT 
    we.executed_at,
    w.name as workflow_name,
    wr.description as rule_description,
    we.invoice_data->>'amount' as amount,
    we.invoice_data->>'department' as department,
    we.action_result
FROM workflow_executions we
JOIN workflows w ON w.id = we.workflow_id
LEFT JOIN workflow_rules wr ON wr.id = we.rule_id
WHERE we.executed_at >= NOW() - INTERVAL '7 days'
ORDER BY we.executed_at DESC;
```

---

## Design Benefits

### 1. Dynamic Configuration
Rules are stored as data, not code. New rules can be added without deploying new code.

### 2. Priority-Based Evaluation
Rules are evaluated in order. First matching rule wins (similar to firewall rules).

### 3. Composable Conditions
Multiple conditions can be combined with AND logic. Could be extended for OR logic.

### 4. Audit Trail
All executions are logged with full invoice data for compliance and debugging.

### 5. Version Control Ready
Adding `version` and `effective_date` columns would enable rule versioning.

### 6. Multi-Tenancy Ready
Adding `tenant_id` to workflows enables multi-tenant deployments.

---

## Future Enhancements

### Support OR Logic
Add a `condition_groups` table:
```sql
CREATE TABLE condition_groups (
    id UUID PRIMARY KEY,
    rule_id UUID REFERENCES workflow_rules(id),
    group_operator VARCHAR(10) -- 'AND' or 'OR'
);

-- Then link conditions to groups instead of directly to rules
ALTER TABLE rule_conditions 
ADD COLUMN condition_group_id UUID REFERENCES condition_groups(id);
```

### Multiple Actions per Rule
Remove the `UNIQUE (rule_id)` constraint from `rule_actions` to allow multiple actions.

### Conditional Actions
Add conditions to actions (e.g., "send to CFO if amount > 50000, else send to Finance Manager").

### Time-Based Rules
Add `effective_from` and `effective_to` dates to rules for scheduling.

### Weighted Priority
Use decimal priorities (1.0, 1.5, 2.0) to allow inserting rules between existing ones.

---

## Technology Choices

- **UUID Primary Keys**: Distributed system friendly, no auto-increment conflicts
- **JSONB for Invoice Data**: Flexible schema for storing complete invoice context
- **GIN Index on JSONB**: Fast querying of invoice data
- **Timestamp with Time Zone**: Critical for distributed systems
- **Foreign Key Cascades**: Maintain referential integrity automatically
- **Composite Unique Constraints**: Prevent duplicate priorities within a workflow
