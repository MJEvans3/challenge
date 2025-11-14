# System Architecture Visualization

## Request Flow Diagram

```
┌─────────────┐
│   Client    │
│  (Browser)  │
└──────┬──────┘
       │ HTTP POST
       │ {"amount": 15000, "department": "marketing",
       │  "requiresManagerApproval": false}
       │
       ▼
┌──────────────────────────────────────────────────────┐
│           WorkflowResource (REST Layer)              │
│  - Validates input                                   │
│  - Converts request to Invoice object                │
│  - Calls WorkflowEngine                              │
│  - Returns WorkflowResponse                          │
└──────────────────┬───────────────────────────────────┘
                   │
                   ▼
┌──────────────────────────────────────────────────────┐
│           WorkflowEngine (Business Logic)            │
│  1. Get rules from repository                        │
│  2. Evaluate rules in priority order                 │
│  3. For each rule:                                   │
│     - Check all conditions (AND logic)               │
│     - If all match → return action                   │
│     - Else → try next rule                           │
└──────────────────┬───────────────────────────────────┘
                   │
                   ▼
┌──────────────────────────────────────────────────────┐
│        WorkflowRepository (Data Layer)               │
│  - Stores 5 workflow rules in memory                 │
│  - Returns rules sorted by priority                  │
└──────────────────┬───────────────────────────────────┘
                   │
                   ▼
┌──────────────────────────────────────────────────────┐
│              WorkflowRule                            │
│  Priority: 1                                         │
│  Conditions: [Amount > 10000, Dept = "marketing"]    │
│  Action: Send to CMO via Email                       │
└──────────────────────────────────────────────────────┘
```

## Class Relationships

```
                    ┌───────────────────────────┐
                    │   Invoice                 │
                    │───────────────────────────│
                    │+ amount                   │
                    │+ department               │
                    │+ requiresManagerApproval  │
                    └───────────────────────────┘
                           │
                           │ evaluated by
                           ▼
    ┌─────────────────────────────────────┐
    │         WorkflowRule                │
    │─────────────────────────────────────│
    │+ id: String                         │
    │+ priority: int                      │
    │+ conditions: List<Condition>        │
    │+ action: ApprovalAction             │
    │─────────────────────────────────────│
    │+ matches(invoice): boolean          │
    └───────────┬─────────────────────────┘
                │
                │ contains
                ▼
    ┌──────────────────────────┐
    │   Condition (Interface)  │
    │──────────────────────────│
    │+ evaluate(Invoice): bool │
    └──────────┬───────────────┘
               │
               │ implementations
               ├─────────────────────┐
               │                     │
               ▼                     ▼
    ┌──────────────────┐   ┌──────────────────┐
    │AmountCondition   │   │DepartmentCond..  │
    │──────────────────│   │──────────────────│
    │- operator        │   │- expected...     │
    │- threshold       │   └──────────────────┘
    └──────────────────┘
               │
               ▼
    ┌──────────────────────────┐
    │   ManagerApprovalCond.   │
    │──────────────────────────│
    │- expectedValue           │
    └──────────────────────────┘

                    ┌──────────────────┐
                    │ ApprovalAction   │
                    │──────────────────│
                    │+ approverRole    │
                    │+ channel         │
                    │+ message         │
                    │──────────────────│
                    │+ execute()       │
                    └──────────────────┘
```

## Workflow Evaluation Flow

```
┌─────────────────────────────────────────────────────┐
│  START: Invoice received                            │
│  {amount: 15000, department: "marketing",           │
│   requiresManagerApproval: false}                   │
└────────────────────┬────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────┐
│  Get all rules sorted by priority                   │
│  [Rule1(p=1), Rule2(p=2), Rule3(p=3), ...]          │
└────────────────────┬────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────┐
│  For each rule (in priority order):                 │
└────────────────────┬────────────────────────────────┘
                     │
                     ▼
         ┌───────────────────────┐
         │  Rule 1 (Priority 1)  │
         │  Conditions:          │
         │  - Amount > 10000     │
         │  - Dept = "marketing" │
         └───────────┬───────────┘
                     │
         ┌───────────▼───────────┐
         │ Evaluate Condition 1  │
         │ Amount > 10000?       │
         │ 15000 > 10000 = TRUE  │
         └───────────┬───────────┘
                     │
         ┌───────────▼───────────┐
         │ Evaluate Condition 2  │
         │ Dept = "marketing"?   │
         │ "marketing" = TRUE    │
         └───────────┬───────────┘
                     │
         ┌───────────▼───────────┐
         │ ALL CONDITIONS TRUE!  │
         │ RULE MATCHES!         │
         └───────────┬───────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────┐
│  Return Action:                                     │
│  - Send to: CMO                                     │
│  - Via: EMAIL                                       │
│  - Message: "High value marketing invoice..."       │
└────────────────────┬────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────┐
│  Execute Action (Print notification)                │
│  "Sending approval request to CMO via EMAIL..."     │
└─────────────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────┐
│  Return Response to Client                          │
│  {"success": true, "approverRole": "CMO", ...}      │
└─────────────────────────────────────────────────────┘
                     │
                     ▼
                  ┌──────┐
                  │ END  │
                  └──────┘
```

## Condition Evaluation Logic

```
Rule Matching Process:

Rule conditions are evaluated with AND logic:
ALL conditions must be TRUE for the rule to match.

Example Rule:
  Conditions: [Amount > 10000, Department = "marketing"]
  
  For Invoice {amount: 15000, department: "marketing"}:
  
  Step 1: Evaluate Amount > 10000
          15000 > 10000 = TRUE ✓
  
  Step 2: Evaluate Department = "marketing"  
          "marketing" = "marketing" = TRUE ✓
  
  Step 3: Combine with AND
          TRUE AND TRUE = TRUE ✓
  
  Result: RULE MATCHES! Return action.

If any condition is FALSE, the rule doesn't match:
  
  For Invoice {amount: 15000, department: "engineering"}:
  
  Step 1: Evaluate Amount > 10000
          15000 > 10000 = TRUE ✓
  
  Step 2: Evaluate Department = "marketing"
          "engineering" = "marketing" = FALSE ✗
  
  Step 3: Combine with AND
          TRUE AND FALSE = FALSE ✗
  
  Result: RULE DOESN'T MATCH. Try next rule.
```

## Complete Workflow Rules (from diagram)

```
Priority 1: Amount > 10000 AND Department = "marketing"
            ↓
            Send to CMO via EMAIL
            
Priority 2: Amount > 10000
            ↓
            Send to CFO via SLACK
            
Priority 3: Amount > 5000 AND Amount <= 10000 AND RequiresManagerApproval = true
            ↓
            Send to FINANCE_MANAGER via EMAIL
            
Priority 4: Amount > 5000 AND Amount <= 10000 AND RequiresManagerApproval = false
            ↓
            Send to FINANCE_TEAM via SLACK
            
Priority 5: Amount <= 5000
            ↓
            Send to FINANCE_TEAM via SLACK
```

## Technology Stack Layers

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  - REST API (JAX-RS / Jersey)           │
│  - JSON Serialization (Jackson)         │
│  - Request/Response DTOs                │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│         Business Logic Layer            │
│  - WorkflowEngine                       │
│  - Condition Evaluation                 │
│  - Rule Matching Logic                  │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│         Data Access Layer               │
│  - WorkflowRepository (In-Memory)       │
│  - Rule Storage & Retrieval             │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│         Domain Model Layer              │
│  - Invoice, WorkflowRule                │
│  - ApprovalAction                       │
│  - Conditions                           │
└─────────────────────────────────────────┘
```
