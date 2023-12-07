import create from "zustand";
import { persist, createJSONStorage } from "zustand/middleware";
import AsyncStorage from "@react-native-async-storage/async-storage";

const useStore = create(
  persist(
    (set, get) => ({
      profileData: null,
      accessToken: null,
      rememberMe: false,
      rememberedEmail: "",
      setProfileData: data => set({ profileData: data }),
      setAccessToken: data => set({ accessToken: data }),
      setRememberedEmail: email => set({ rememberedEmail: email }),
      setRememberMe: data => set({ rememberMe: true }),
      clearAuth: () => {
        set(_ => ({
          accessToken: null,
          profileData: null,
          // rememberMe: false,
        }));
      },
    }),
    {
      name: "user-store", // unique name of the store
      storage: createJSONStorage(() => AsyncStorage),
    }
  )
);

export default useStore;
