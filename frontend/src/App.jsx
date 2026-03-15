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
import Workspaces from "./components/admin/Workspaces";
import ProjectsAdmin from "./components/admin/ProjectsAdmin";
import MyWorkspace from "./components/user/MyWorkspace";
import Projects from "./components/user/Projects";
import ProjectBoard from "./components/user/ProjectBoard";
import ProjectMembers from "./components/user/ProjectMembers";

const App = () => {
  const [user, dispatch] = useReducer(UserReducer, null);

  useEffect(() => {
    const token = cookie.load("token");

    if (token) {
      dispatch({
        type: "login",
      });
    }
  }, []);

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
            <Route path="/admin/workspaces" element={<Workspaces />} />
            <Route path="/admin/projects" element={<ProjectsAdmin />} />
            <Route path="/workspaces/me" element={<MyWorkspace />} />
            <Route path="/projects" element={<Projects />} />
            <Route
              path="/projects/:projectId/board"
              element={<ProjectBoard />}
            />
            <Route
              path="/projects/:projectId/members"
              element={<ProjectMembers />}
            />
          </Routes>
        </Container>

        <Footer />
      </BrowserRouter>
    </MyUserContext.Provider>
  );
};

export default App;
