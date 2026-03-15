import axios from "axios";
import cookie from "react-cookies";

const BASE_URL = "http://localhost:8080/";

export const endpoints = {
  register: "/auth/users",
  login: "/auth/token",
  "refresh-token": "/auth/refresh",
  logout: "/auth/logout",
  "forget-password": "/auth/forgot-password",
  "reset-password": "/auth/reset-password",
  me: "/users/me",
  "change-password": "/users/me/change-password",
  "profile-me": "/profiles/me",
  "update-profile": "/profiles/me",
  "update-avatar": "/profiles/me/avatar",
  permissions: "/auth/permissions",
  roles: "/auth/roles",
  users: "/auth/users",
  profiles: "/profiles",
  workspaces: "/workspaces",
  "my-workspace": "/workspaces/me",
  "update-workspace": "/workspaces/me",
  "my-projects": "/workspaces/me/projects",
  "remove-project-from-workspace": (projectId) =>
    `/workspaces/me/projects/${projectId}`,
  projects: "/projects",
  project: (projectId) => `/projects/${projectId}`,
  "project-members": (projectId) => `/projects/${projectId}/members`,
  "project-member": (projectId, userId) =>
    `/projects/${projectId}/members/${userId}`,
  "project-columns": (projectId) => `/projects/${projectId}/columns`,
  "project-column": (projectId, columnId) =>
    `/projects/${projectId}/columns/${columnId}`,
};

export const authApis = () => {
  return axios.create({
    baseURL: BASE_URL,
    headers: {
      Authorization: `Bearer ${cookie.load("token")}`,
    },
  });
};

export default axios.create({
  baseURL: BASE_URL,
});
