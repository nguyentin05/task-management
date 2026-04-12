import axios from "axios";
import cookie from "react-cookies";

const BASE_URL = import.meta.env.API_URL || "http://localhost:8888/api/v1";

export const endpoints = {
  login: "/auth/token",
  "refresh-token": "/auth/refresh",
  logout: "/auth/logout",
  register: "/auth/users/register",
  "get-all-roles": "/auth/roles",
  "create-user": "/auth/users",
  "get-all-users": "/auth/users",
  "get-user": (userId) => `/auth/users/${userId}`,
  "reset-password": (userId) => `/auth/users/${userId}/reset-password`,
  "change-role": (userId) => `/auth/users/${userId}/roles`,
  me: "/auth/users/me",
  "change-password": "/auth/users/me/change-password",
  "delete-user": (userId) => `/auth/users/${userId}`,
  "profiles-me": "/profiles/me",
  "update-profiles": "/profiles/me",
  "get-all-profiles": "/profiles",
  "get-profile": (profileId) => `/profiles/${profileId}`,
  "update-profile": (profileId) => `/profiles/${profileId}`,
  "update-avatar-me": "/profiles/me/avatar",
  "update-avatar": (profileId) => `/profiles/${profileId}/avatar`,
  "workspaces-me": "/workspaces/me",
  "workspaces-me-projects": "/workspaces/me/projects",
  "delete-workspaces-me-projects": (projectId) =>
    `workspaces/me/projects/${projectId}`,
  "get-all-workspaces": "/workspaces",
  "get-workspaces": (workspaceId) => `/workspaces/${workspaceId}`,
  "get-projects-in-workspace": (workspaceId) =>
    `/workspaces/${workspaceId}/projects`,
  "update-workspace": (workspaceId) => `/workspaces/${workspaceId}`,
  "delete-project-in-workspace": (workspaceId, projectId) =>
    `/workspaces/${workspaceId}/projects/${projectId}`,
  "create-project": "/projects",
  "get-all-projects": "/projects",
  "get-project": (projectId) => `/projects/${projectId}`,
  "update-project": (projectId) => `/projects/${projectId}`,
  "delete-project": (projectId) => `/projects/${projectId}`,
  "get-all-member": (projectId) => `/projects/${projectId}/members`,
  "add-member": (projectId) => `/projects/${projectId}/members`,
  "search-member": (projectId) => `/projects/${projectId}/members/search`,
  statistics: (projectId) => `/projects/${projectId}/statistics`,
  "change-member-role": (projectId, userId) =>
    `/projects/${projectId}/members/${userId}`,
  "delete-member": (projectId, userId) =>
    `/projects/${projectId}/members/${userId}`,
  "create-column": (projectId) => `/projects/${projectId}/columns`,
  "get-kanban-board": (projectId) => `/projects/${projectId}/columns`,
  "change-column-name": (projectId, columnId) =>
    `/projects/${projectId}/columns/${columnId}`,
  "move-column": (projectId, columnId) =>
    `/projects/${projectId}/columns/${columnId}`,
  "delete-column": (projectId, columnId) =>
    `/projects/${projectId}/columns/${columnId}`,
  "create-task": (columnId) => `/columns/${columnId}/tasks`,
  "get-task": (taskId) => `/tasks/${taskId}`,
  "update-task": (taskId) => `/tasks/${taskId}`,
  "move-task": (taskId) => `/tasks/${taskId}/move`,
  assignees: (taskId) => `/tasks/${taskId}/assignees`,
  "delete-assignees": (taskId, userId) =>
    `/tasks/${taskId}/assignees/${userId}`,
  "delete-task": (taskId) => `/tasks/${taskId}`,
  "create-comment": (taskId) => `/tasks/${taskId}/comments`,
  "get-all-comments": (taskId) => `/tasks/${taskId}/comments`,
  "edit-comment": (commentId) => `/comments/${commentId}`,
  "delete-comment": (commentId) => `/comments/${commentId}`,
};

const refreshToken = async () => {
  const token = cookie.load("token");
  if (!token) return null;

  try {
    const res = await axios.post(`${BASE_URL}${endpoints["refresh-token"]}`, {
      token: token,
    });

    if (res.data.code === 1000) {
      const newToken = res.data.result.token;
      cookie.save("token", newToken);
      return newToken;
    }
  } catch (err) {
    console.error("Không lấy được token mới!", err);
    cookie.remove("token");
  }
  return null;
};

export const authApis = () => {
  const instance = axios.create({ baseURL: BASE_URL });

  instance.interceptors.request.use((config) => {
    const token = cookie.load("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });

  instance.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config;

      if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;
        const newToken = await refreshToken();

        if (newToken) {
          originalRequest.headers["Authorization"] = `Bearer ${newToken}`;
          return instance(originalRequest);
        } else {
          cookie.remove("token");
          window.location.href = "/login";
        }
      }
      return Promise.reject(error);
    },
  );

  return instance;
};

export default axios.create({
  baseURL: BASE_URL,
});
