import axios from "axios";
import cookie from "react-cookies";

const BASE_URL = import.meta.env.API_URL || "http://localhost:8888/api/v1";

export const endpoints = {
  login: "/auth/token",
  "refresh-token": "/auth/refresh",
  logout: "/auth/logout",
  register: "/auth/users/register",
  roles: "/auth/roles",
  
  users: "/auth/users",
  user: (userId) => `/auth/users/${userId}`,
  "user-roles": (userId) => `/auth/users/${userId}/roles`,
  "reset-password": (userId) => `/auth/users/${userId}/reset-password`,
  
  me: "/auth/users/me",
  "change-password": "/auth/users/me/change-password",

  profiles: "/profiles",
  profile: (profileId) => `/profiles/${profileId}`,
  "profile-avatar": (profileId) => `/profiles/${profileId}/avatar`,
  
  "profiles-me": "/profiles/me",
  "update-profiles": "/profiles/me",
  "update-avatar-me": "/profiles/me/avatar",

  workspaces: "/workspaces",
  workspace: (workspaceId) => `/workspaces/${workspaceId}`,
  "workspace-projects": (workspaceId) => `/workspaces/${workspaceId}/projects`,
  "remove-workspace-project": (workspaceId, projectId) => `/workspaces/${workspaceId}/projects/${projectId}`,
  
  "my-workspace": "/workspaces/me",
  "update-workspace": "/workspaces/me",
  "my-projects": "/workspaces/me/projects",
  "remove-project-from-workspace": (projectId) => `/workspaces/me/projects/${projectId}`,

  projects: "/projects",
  project: (projectId) => `/projects/${projectId}`,
  
  "project-members": (projectId) => `/projects/${projectId}/members`,
  "project-member": (projectId, userId) => `/projects/${projectId}/members/${userId}`,
  "project-members-search": (projectId) => `/projects/${projectId}/members/search`,
  "project-statistics": (projectId) => `/projects/${projectId}/statistics`,

  "project-columns": (projectId) => `/projects/${projectId}/columns`,
  "project-column": (projectId, columnId) => `/projects/${projectId}/columns/${columnId}`,
  
  "column-tasks": (columnId) => `/columns/${columnId}/tasks`,
  task: (taskId) => `/tasks/${taskId}`,
  "move-task": (taskId) => `/tasks/${taskId}/move`,
  
  "task-assignees": (taskId) => `/tasks/${taskId}/assignees`,
  "task-assignee": (taskId, userId) => `/tasks/${taskId}/assignees/${userId}`,

  "task-comments": (taskId) => `/tasks/${taskId}/comments`,
  comment: (commentId) => `/comments/${commentId}`,
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
    }
  );

  return instance;
};

export default axios.create({
  baseURL: BASE_URL,
});