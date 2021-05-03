package com.agyohora.mobileperitc.data.network.model;


/**
 * Created by Invent on 2-1-18.
 */

public class LoginRequest {


    public static class ServerLoginRequest {
        private String email;
        private String password;

        public ServerLoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;

            ServerLoginRequest that = (ServerLoginRequest) object;

            return (email != null ? email.equals(that.email) : that.email == null) && (password != null ? password.equals(that.password) : that.password == null);

        }

        @Override
        public int hashCode() {
            int result = email != null ? email.hashCode() : 0;
            result = 31 * result + (password != null ? password.hashCode() : 0);
            return result;
        }
    }
}