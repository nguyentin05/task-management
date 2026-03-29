import { BrowserRouter, Route, Routes, Navigate, Outlet } from "react-router-dom";
import { MyUserContext } from "./configs/MyContexts";
import { useReducer, useEffect } from "react";
import { UserReducer } from "./reducers/MyUserReducer";
import { Container } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import cookie from "react-cookies";
import Header from "./components/layout/Header";
import Footer from "./components/layout/Footer";
import Register from "./components/Register";
import Login from "./components/Login";
import Profile from "./components/Profile";
import Workspace from "./components/Workspace";
import BoardView from "./components/BoardView";
import Admin from "./components/admin/Admin";
import Users from "./components/admin/Users";
import Workspaces from "./components/admin/Workspaces";
import Projects from "./components/admin/Projects";
import Apis, { endpoints } from "./configs/Apis";

const PrivateRoute = ({ user }) => {
  return user ? <Outlet /> : <Navigate to="/login" replace />;
};

const AdminRoute = ({ user }) => {
  if (!user) return <Navigate to="/login" replace />;
  const isAdmin = user.roles?.some((role) => role.name === "ADMIN");
  return isAdmin ? <Outlet /> : <Navigate to="/w/me" replace />;
};

const App = () => {
  const [user, dispatch] = useReducer(UserReducer, null);

  useEffect(() => {
    const token = cookie.load("token");
    if (token) {
      Apis.get(endpoints["me"], {
        headers: { Authorization: `Bearer ${token}` },
      })
        .then((res) => dispatch({ type: "login", payload: res.data.result }))
        .catch(() => cookie.remove("token"));
    }
  }, []);

  return (
    <MyUserContext.Provider value={[user, dispatch]}>
      <BrowserRouter>
        <Header />

        <Container>
          <Routes>
            <Route
              path="/"
              element={<Navigate to={user ? "/w/me" : "/login"} replace />}
            />
            <Route
              path="/register"
              element={user ? <Navigate to="/w/me" replace /> : <Register />}
            />
            <Route
              path="/login"
              element={user ? <Navigate to="/w/me" replace /> : <Login />}
            />
            <Route element={<PrivateRoute user={user} />}>
              <Route path="/me" element={<Profile />} />
              <Route path="/w/me" element={<Workspace />} />
              <Route path="/p/:projectId" element={<BoardView />} />
            </Route>
            <Route element={<AdminRoute user={user} />}>
              <Route path="/admin" element={<Admin />} />
              <Route path="/admin/users" element={<Users />} />
              <Route path="/admin/workspaces" element={<Workspaces />} />
              <Route
                path="/admin/workspaces/:workspaceId/projects"
                element={<Projects />}
              />
            </Route>
          </Routes>
        </Container>

        <Footer />
      </BrowserRouter>
    </MyUserContext.Provider>
  );
};

export default App;