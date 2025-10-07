/* -------------------------------------------- $stores -- ;

     \\     Include all stores here, each in their own
     (o>    file. You could also use folders if it makes
  \\_//)    sense.
   \_/_)
    _|_     See /zustand for dynamic listing of stores. 

* ------------------------------------------------------- */

import { create } from "zustand";
import { createJSONStorage, devtools, persist } from "zustand/middleware";

import createAuthSlice from "./authSlice";

const useBoundStore = create<
        AuthState
>()(
    devtools(
        persist(
            (...a) => ({
                ...createAuthSlice(...a),
            }),
            { name: "app-storage", storage: createJSONStorage(() => sessionStorage) },
        ),
    ),
);

export default useBoundStore;

/* ----------------------------------------------- types -- */


import type { AuthState } from "./authSlice";


export const getAuthState = () => useBoundStore.getState();
