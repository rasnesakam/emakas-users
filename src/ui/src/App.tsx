
import './App.css'
import {BrowserRouter} from "react-router";
import {AppRouter} from "@routes/AppRouter.tsx";
import {AuthContextAdapter} from "./contexts/AuthContext";

function App() {

  return (
    <>
      <AuthContextAdapter>
          <BrowserRouter>
              <AppRouter/>
          </BrowserRouter>
      </AuthContextAdapter>
    </>
  )
}

export default App
