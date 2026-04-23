
import './App.css'
import {BrowserRouter} from "react-router";
import {AppRouter} from "@routes/AppRouter.tsx";
import {AuthContextAdapter} from "./contexts/AuthContext";
import {AlertBoxProvider} from "@contexts/AlertBoxContext";

function App() {

  return (
    <>
      <AuthContextAdapter>
          <AlertBoxProvider>
              <BrowserRouter>
                  <AppRouter/>
              </BrowserRouter>
          </AlertBoxProvider>
      </AuthContextAdapter>
    </>
  )
}

export default App
