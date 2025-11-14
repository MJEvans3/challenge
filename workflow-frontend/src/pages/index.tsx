import { useState } from 'react';
import Head from 'next/head';
import { WorkflowRequest, WorkflowResult } from '../types/workflow';

export default function Home() {
  const [amount, setAmount] = useState<string>('');
  const [department, setDepartment] = useState<string>('');
  const [requiresManagerApproval, setRequiresManagerApproval] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(false);
  const [result, setResult] = useState<WorkflowResult | null>(null);
  const [error, setError] = useState<string | null>(null);

  const departments = [
    'marketing',
    'engineering',
    'operations',
    'sales',
    'hr',
    'finance',
    'legal',
    'admin'
  ];

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setResult(null);

    try {
      const requestData: WorkflowRequest = {
        amount: parseFloat(amount),
        department: department,
        requiresManagerApproval: requiresManagerApproval
      };

      const response = await fetch('/api/workflow', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestData),
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.error || 'Failed to evaluate workflow');
      }

      setResult({
        ...data,
        timestamp: new Date().toISOString()
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred');
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setAmount('');
    setDepartment('');
    setRequiresManagerApproval(false);
    setResult(null);
    setError(null);
  };

  const getChannelIcon = (channel?: string) => {
    if (channel === 'SLACK') return '💬';
    if (channel === 'EMAIL') return '📧';
    return '📬';
  };

  const getApproverIcon = (role?: string) => {
    if (role === 'CMO') return '👔';
    if (role === 'CFO') return '💼';
    if (role === 'FINANCE_MANAGER') return '📊';
    if (role === 'FINANCE_TEAM') return '👥';
    return '👤';
  };

  return (
    <>
      <Head>
        <title>Invoice Approval Workflow</title>
        <meta name="description" content="Dynamic invoice approval workflow system" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
      </Head>

      <main className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 py-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-4xl mx-auto">
          {/* Header */}
          <div className="text-center mb-8">
            <h1 className="text-4xl font-bold text-gray-900 mb-2">
              Invoice Approval Workflow
            </h1>
            <p className="text-lg text-gray-600">
              Dynamic rule-based approval system
            </p>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Input Form */}
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-2xl font-semibold text-gray-900 mb-6">
                Invoice Details
              </h2>

              <form onSubmit={handleSubmit} className="space-y-6">
                {/* Amount Input */}
                <div>
                  <label htmlFor="amount" className="block text-sm font-medium text-gray-700 mb-2">
                    Invoice Amount (USD)
                  </label>
                  <div className="relative">
                    <span className="absolute left-3 top-3 text-gray-500 font-medium">$</span>
                    <input
                      type="number"
                      id="amount"
                      value={amount}
                      onChange={(e) => setAmount(e.target.value)}
                      placeholder="0.00"
                      step="0.01"
                      min="0"
                      required
                      className="w-full pl-8 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 text-lg"
                    />
                  </div>
                  <p className="mt-1 text-xs text-gray-500">
                    Enter the invoice amount in USD
                  </p>
                </div>

                {/* Department Select */}
                <div>
                  <label htmlFor="department" className="block text-sm font-medium text-gray-700 mb-2">
                    Department
                  </label>
                  <select
                    id="department"
                    value={department}
                    onChange={(e) => setDepartment(e.target.value)}
                    required
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 text-lg"
                  >
                    <option value="">Select department...</option>
                    {departments.map((dept) => (
                      <option key={dept} value={dept}>
                        {dept.charAt(0).toUpperCase() + dept.slice(1)}
                      </option>
                    ))}
                  </select>
                </div>

                {/* Manager Approval Checkbox */}
                <div>
                  <label className="flex items-start space-x-3 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={requiresManagerApproval}
                      onChange={(e) => setRequiresManagerApproval(e.target.checked)}
                      className="mt-1 h-5 w-5 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
                    />
                    <div>
                      <span className="block text-sm font-medium text-gray-900">
                        Requires Manager Approval
                      </span>
                      <span className="block text-xs text-gray-500">
                        Check if this invoice requires explicit manager approval
                      </span>
                    </div>
                  </label>
                </div>

                {/* Buttons */}
                <div className="flex space-x-3 pt-4">
                  <button
                    type="submit"
                    disabled={loading}
                    className="flex-1 bg-indigo-600 text-white py-3 px-6 rounded-lg font-semibold hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                  >
                    {loading ? (
                      <span className="flex items-center justify-center">
                        <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                        Evaluating...
                      </span>
                    ) : (
                      'Evaluate Workflow'
                    )}
                  </button>
                  <button
                    type="button"
                    onClick={handleReset}
                    className="px-6 py-3 border border-gray-300 rounded-lg font-semibold text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500 transition-colors"
                  >
                    Reset
                  </button>
                </div>
              </form>
            </div>

            {/* Results Panel */}
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-2xl font-semibold text-gray-900 mb-6">
                Approval Result
              </h2>

              {!result && !error && (
                <div className="flex flex-col items-center justify-center h-64 text-gray-400">
                  <svg className="w-16 h-16 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                  <p className="text-center">
                    Enter invoice details and click<br />
                    <span className="font-semibold">Evaluate Workflow</span> to see results
                  </p>
                </div>
              )}

              {error && (
                <div className="bg-red-50 border border-red-200 rounded-lg p-4">
                  <div className="flex">
                    <div className="flex-shrink-0">
                      <svg className="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
                        <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
                      </svg>
                    </div>
                    <div className="ml-3">
                      <h3 className="text-sm font-medium text-red-800">Error</h3>
                      <p className="mt-1 text-sm text-red-700">{error}</p>
                    </div>
                  </div>
                </div>
              )}

              {result && result.success && (
                <div className="space-y-4">
                  {/* Success Banner */}
                  <div className="bg-green-50 border border-green-200 rounded-lg p-4">
                    <div className="flex items-center">
                      <svg className="h-5 w-5 text-green-400 mr-2" viewBox="0 0 20 20" fill="currentColor">
                        <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                      </svg>
                      <span className="text-sm font-medium text-green-800">
                        Workflow evaluated successfully
                      </span>
                    </div>
                  </div>

                  {/* Approval Details */}
                  <div className="space-y-3">
                    <div className="bg-gray-50 rounded-lg p-4">
                      <div className="flex items-center mb-2">
                        <span className="text-2xl mr-2">{getApproverIcon(result.approverRole)}</span>
                        <div>
                          <p className="text-xs text-gray-500 font-medium uppercase tracking-wide">Approver</p>
                          <p className="text-lg font-semibold text-gray-900">
                            {result.approverRole?.replace(/_/g, ' ')}
                          </p>
                        </div>
                      </div>
                    </div>

                    <div className="bg-gray-50 rounded-lg p-4">
                      <div className="flex items-center mb-2">
                        <span className="text-2xl mr-2">{getChannelIcon(result.channel)}</span>
                        <div>
                          <p className="text-xs text-gray-500 font-medium uppercase tracking-wide">Notification Channel</p>
                          <p className="text-lg font-semibold text-gray-900">
                            {result.channel}
                          </p>
                        </div>
                      </div>
                    </div>

                    <div className="bg-indigo-50 rounded-lg p-4 border border-indigo-200">
                      <p className="text-xs text-indigo-600 font-medium uppercase tracking-wide mb-2">Message</p>
                      <p className="text-sm text-gray-900 leading-relaxed">
                        {result.message}
                      </p>
                    </div>
                  </div>

                  {/* Summary */}
                  <div className="mt-6 pt-4 border-t border-gray-200">
                    <p className="text-xs text-gray-500 text-center">
                      Evaluated at {new Date(result.timestamp).toLocaleString()}
                    </p>
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Info Section */}
          <div className="mt-8 bg-white rounded-xl shadow-lg p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">How It Works</h3>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="text-center p-4">
                <div className="text-3xl mb-2">💰</div>
                <h4 className="font-semibold text-gray-900 mb-1">Amount Threshold</h4>
                <p className="text-sm text-gray-600">
                  Different approval levels based on invoice amount
                </p>
              </div>
              <div className="text-center p-4">
                <div className="text-3xl mb-2">🏢</div>
                <h4 className="font-semibold text-gray-900 mb-1">Department Rules</h4>
                <p className="text-sm text-gray-600">
                  Special routing for marketing and other departments
                </p>
              </div>
              <div className="text-center p-4">
                <div className="text-3xl mb-2">✅</div>
                <h4 className="font-semibold text-gray-900 mb-1">Manager Override</h4>
                <p className="text-sm text-gray-600">
                  Requires manager approval for specific cases
                </p>
              </div>
            </div>
          </div>

          {/* Workflow Rules Section */}
          <div className="mt-8 bg-white rounded-xl shadow-lg p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Workflow Rules</h3>
            <div className="space-y-3">
              {/* Rule 1 */}
              <div className="flex items-start p-4 bg-blue-50 rounded-lg border border-blue-200">
                <div className="flex-shrink-0 w-8 h-8 bg-blue-600 text-white rounded-full flex items-center justify-center font-bold text-sm mr-3">
                  1
                </div>
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-1">
                    <span className="font-semibold text-gray-900">Amount &gt; $10,000 AND Marketing Department</span>
                  </div>
                  <div className="flex items-center gap-2 text-sm">
                    <span className="text-gray-600">→</span>
                    <span className="px-2 py-1 bg-blue-100 text-blue-800 rounded font-medium">CMO</span>
                    <span className="text-gray-600">via</span>
                    <span className="px-2 py-1 bg-gray-100 text-gray-800 rounded font-medium">📧 EMAIL</span>
                  </div>
                </div>
              </div>

              {/* Rule 2 */}
              <div className="flex items-start p-4 bg-purple-50 rounded-lg border border-purple-200">
                <div className="flex-shrink-0 w-8 h-8 bg-purple-600 text-white rounded-full flex items-center justify-center font-bold text-sm mr-3">
                  2
                </div>
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-1">
                    <span className="font-semibold text-gray-900">Amount &gt; $10,000 AND NOT Marketing</span>
                  </div>
                  <div className="flex items-center gap-2 text-sm">
                    <span className="text-gray-600">→</span>
                    <span className="px-2 py-1 bg-purple-100 text-purple-800 rounded font-medium">CFO</span>
                    <span className="text-gray-600">via</span>
                    <span className="px-2 py-1 bg-gray-100 text-gray-800 rounded font-medium">💬 SLACK</span>
                  </div>
                </div>
              </div>

              {/* Rule 3 */}
              <div className="flex items-start p-4 bg-green-50 rounded-lg border border-green-200">
                <div className="flex-shrink-0 w-8 h-8 bg-green-600 text-white rounded-full flex items-center justify-center font-bold text-sm mr-3">
                  3
                </div>
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-1">
                    <span className="font-semibold text-gray-900">$5,000 &lt; Amount ≤ $10,000 AND Requires Manager Approval</span>
                  </div>
                  <div className="flex items-center gap-2 text-sm">
                    <span className="text-gray-600">→</span>
                    <span className="px-2 py-1 bg-green-100 text-green-800 rounded font-medium">Finance Manager</span>
                    <span className="text-gray-600">via</span>
                    <span className="px-2 py-1 bg-gray-100 text-gray-800 rounded font-medium">📧 EMAIL</span>
                  </div>
                </div>
              </div>

              {/* Rule 4 */}
              <div className="flex items-start p-4 bg-yellow-50 rounded-lg border border-yellow-200">
                <div className="flex-shrink-0 w-8 h-8 bg-yellow-600 text-white rounded-full flex items-center justify-center font-bold text-sm mr-3">
                  4
                </div>
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-1">
                    <span className="font-semibold text-gray-900">$5,000 &lt; Amount ≤ $10,000 AND NO Manager Approval</span>
                  </div>
                  <div className="flex items-center gap-2 text-sm">
                    <span className="text-gray-600">→</span>
                    <span className="px-2 py-1 bg-yellow-100 text-yellow-800 rounded font-medium">Finance Team</span>
                    <span className="text-gray-600">via</span>
                    <span className="px-2 py-1 bg-gray-100 text-gray-800 rounded font-medium">💬 SLACK</span>
                  </div>
                </div>
              </div>

              {/* Rule 5 */}
              <div className="flex items-start p-4 bg-gray-50 rounded-lg border border-gray-200">
                <div className="flex-shrink-0 w-8 h-8 bg-gray-600 text-white rounded-full flex items-center justify-center font-bold text-sm mr-3">
                  5
                </div>
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-1">
                    <span className="font-semibold text-gray-900">Amount ≤ $5,000</span>
                  </div>
                  <div className="flex items-center gap-2 text-sm">
                    <span className="text-gray-600">→</span>
                    <span className="px-2 py-1 bg-gray-100 text-gray-800 rounded font-medium">Finance Team</span>
                    <span className="text-gray-600">via</span>
                    <span className="px-2 py-1 bg-gray-100 text-gray-800 rounded font-medium">💬 SLACK</span>
                  </div>
                </div>
              </div>
            </div>
            <div className="mt-4 p-3 bg-indigo-50 rounded-lg border border-indigo-200">
              <p className="text-xs text-indigo-800">
                <span className="font-semibold">Note:</span> Rules are evaluated in priority order. The first matching rule determines the approval workflow.
              </p>
            </div>
          </div>
        </div>
      </main>
    </>
  );
}
