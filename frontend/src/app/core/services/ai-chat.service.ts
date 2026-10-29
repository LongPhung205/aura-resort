import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { ChatApiResponse } from '../models/ai-chat.model';

@Injectable({
  providedIn: 'root'
})
export class AiChatService {

  private readonly apiUrl = `${environment.apiUrl}/ai`;

  constructor(private http: HttpClient) {}

  sendMessage(sessionId: string, message: string): Observable<ChatApiResponse> {
    return this.http.post<{ data: ChatApiResponse }>(`${this.apiUrl}/chat`, {
      sessionId,
      message
    }).pipe(map(res => res.data));
  }

  clearSession(sessionId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/chat/session/${sessionId}`);
  }

  generateSessionId(): string {
    return 'session-' + Date.now() + '-' + Math.random().toString(36).substring(2, 9);
  }

  getOrCreateSessionId(): string {
    const key = 'ai_chat_session_id';
    let sessionId = localStorage.getItem(key);
    if (!sessionId) {
      sessionId = this.generateSessionId();
      localStorage.setItem(key, sessionId);
    }
    return sessionId;
  }

  resetSession(): void {
    const key = 'ai_chat_session_id';
    const sessionId = localStorage.getItem(key);
    if (sessionId) {
      this.clearSession(sessionId).subscribe();
    }
    const newId = this.generateSessionId();
    localStorage.setItem(key, newId);
  }
}
