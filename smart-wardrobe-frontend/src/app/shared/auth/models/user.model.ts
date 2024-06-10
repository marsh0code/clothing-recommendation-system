export interface User {
  username: string;
  email: string;
  imageUrl: string;
  id: number;
  createdAt: number;
}

export interface UserCredentials {
  token: string;
  type: string;
  algorithm: string;
  expiresAt: string;
}

