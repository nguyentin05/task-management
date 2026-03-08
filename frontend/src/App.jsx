import { BrowserRouter, Route, Routes } from "react-router-dom";
import Header from "./components/layout/Header";
import Footer from "./components/layout/Footer";
import Home from "./components/Home";
import Register from "./components/Register";
import Login from "./components/Login";
import Profile from "./components/Profile";
import { MyUserContext } from "./configs/MyContexts";
import { useReducer, useEffect } from "react";
import { UserReducer } from "./reducers/MyUserReducer";
import { Container } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import cookie from "react-cookies";
import Admin from "./components/Admin";
import Permissions from "./components/admin/Permissions";
import Roles from "./components/admin/Roles";
import Users from "./components/admin/Users";
import Profiles from "./components/admin/Profiles";

const App = () => {
  const [user, dispatch] = useReducer(UserReducer, null);

  useEffect(() => {
    const token = cookie.load("token");

    if (token) {
      dispatch({
        type: "login",
      });
    }
  });

  return (
    <MyUserContext.Provider value={[user, dispatch]}>
      <BrowserRouter>
        <Header />

        <Container>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/register" element={<Register />} />
            <Route path="/login" element={<Login />} />
            <Route path="/profiles/me" element={<Profile />} />
            <Route path="/admin" element={<Admin />} />
            <Route path="/admin/permissions" element={<Permissions />} />
            <Route path="/admin/roles" element={<Roles />} />
            <Route path="/admin/users" element={<Users />} />
            <Route path="/admin/profiles" element={<Profiles />} />
          </Routes>
        </Container>

        <Footer />
      </BrowserRouter>
    </MyUserContext.Provider>
  );
};

export default App;
