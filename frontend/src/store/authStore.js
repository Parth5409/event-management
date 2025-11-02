import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import { jwtDecode } from 'jwt-decode';
import { authApi } from '../lib/api';

export const useAuthStore = create(
  persist(
    (set, get) => ({
      token: null,
      user: null,
      isAuthenticated: false,

      login: async (token) => {
        try {
          const decodedToken = jwtDecode(token);
          const userResponse = await authApi.getUser(decodedToken.sub);
          
          set({ token, user: userResponse.data, isAuthenticated: true });
        } catch (error) {
          console.error("Login process failed:", error);
          get().logout(); // Clear state on error
        }
      },

      logout: () => {
        set({ token: null, user: null, isAuthenticated: false });
      },

      updateUser: (newUser) => {
        set((state) => ({
          user: { ...state.user, ...newUser },
        }));
      },
    }),
    {
      name: 'auth-storage', // name of the item in localStorage
      storage: createJSONStorage(() => localStorage),
    }
  )
);
