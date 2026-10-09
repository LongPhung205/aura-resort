export interface AuthResponse {
  accessToken: string;
  refreshToken?: string;
  tokenType?: string;
  role?: string;
  fullName?: string;
  avatarUrl?: string;
}

export interface ApiResponse<T> {
  success?: boolean;
  status?: string;
  message: string;
  data: T;
}

export interface LoginRequest {
  email?: string;
  password?: string;
}

export interface RegisterRequest {
  fullName?: string;
  email?: string;
  password?: string;
}

export interface VerifyOtpRequest {
  email?: string;
  otp?: string;
}

export interface ForgotPasswordRequest {
  email?: string;
}

export interface ResetPasswordRequest {
  email?: string;
  otp?: string;
  newPassword?: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface LogoutRequest {
  refreshToken?: string;
}
