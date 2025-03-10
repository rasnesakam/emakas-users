
import './App.css'
import {BrowserRouter} from "react-router";
import {AppRouter} from "@routes/AppRouter.tsx";

function App() {

  return (
    <>
      <BrowserRouter>
          <AppRouter/>
      </BrowserRouter>
    </>
  )
}

export default App
