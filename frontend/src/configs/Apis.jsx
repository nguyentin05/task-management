import axios from "axios";
import cookie from "react-cookies";

const BASE_URL = "http://localhost:8080/";

export const endpoints = {
  register: "/auth/register",
  login: "/auth/login",
  "refresh-token": "/auth/refresh",
  logout: "/auth/logout",
  "forget-password": "/auth/forgot-password",
  "reset-password": "/auth/reset-password",
  profile: "users/me",
  "change-password": "users/me/change-password",
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
