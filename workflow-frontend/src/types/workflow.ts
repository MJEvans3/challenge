// API types for workflow system

export interface WorkflowRequest {
  amount: number;
  department: string;
  requiresManagerApproval: boolean;
}

export interface WorkflowResponse {
  success: boolean;
  approverRole?: string;
  channel?: string;
  message?: string;
  error?: string;
}

export interface WorkflowResult extends WorkflowResponse {
  timestamp: string;
}
