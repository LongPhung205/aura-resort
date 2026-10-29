/* eslint-disable @typescript-eslint/no-explicit-any */
export interface ChatMessage {
  role: 'user' | 'ai';
  content: string;
  timestamp: Date;
  actionType?: string | null;
  actionPayload?: any;
  isLoading?: boolean;
}

export interface ChatApiResponse {
  reply: string;
  sessionId: string;
  actionType?: string | null;
  actionPayload?: any;
}
