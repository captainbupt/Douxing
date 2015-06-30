package com.badou.mworking.domain;

import com.badou.mworking.net.RestRepository;

import rx.Observable;

public class LoginUseCase extends UseCase {

    String username;
    String password;
    String latitude;
    String longitude;

    public LoginUseCase(String username, String password, String latitude, String longitude) {
        this.username = username;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().login(new Login(username, password, new Location(latitude, longitude)));
    }

    public static class Login {
        String serial;
        String pwd;
        Location gps;

        public Login(String serial, String pwd, Location gps) {
            this.serial = serial;
            this.pwd = pwd;
            this.gps = gps;
        }
    }

    static class Location {
        String lat;
        String lon;

        public Location(String lat, String lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }
}
