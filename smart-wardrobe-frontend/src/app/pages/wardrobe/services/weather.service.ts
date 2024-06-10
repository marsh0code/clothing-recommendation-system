import { effect, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { take, tap } from 'rxjs';
import { AuthService } from '../../../shared/auth/services';

interface WeatherData {
  app_temp: number;
  aqi: number;
  city_name: string;
  clouds: number;
  country_code: string;
  datetime: string;
  dewpt: number;
  dhi: number;
  dni: number;
  elev_angle: number;
  ghi: number;
  gust: number;
  h_angle: number;
  lat: number;
  lon: number;
  ob_time: string;
  pod: string;
  precip: number;
  pres: number;
  rh: number;
  slp: number;
  snow: number;
  solar_rad: number;
  sources: string[];
  state_code: string;
  station: string;
  sunrise: string;
  sunset: string;
  temp: number;
  timezone: string;
  ts: number;
  uv: number;
  vis: number;
  weather: {
    icon: string;
    description: string;
    code: number;
  };
  wind_cdir: string;
  wind_cdir_full: string;
  wind_dir: number;
  wind_spd: number;
}

interface CurrentWeatherApiResponse {
  count: number;
  data: WeatherData[];
}


@Injectable({ providedIn: 'root' })
export class WeatherService {
  private _currentWeather$ = signal<WeatherData | null>(null);
  private _doesLocationAllowed$ = signal<boolean>(true);
  private _httpClient = inject(HttpClient);
  private _authService = inject(AuthService);

  get currentWeather$() {
    return this._currentWeather$.asReadonly();
  }

  get doesLocationAllowed$() {
    return this._doesLocationAllowed$.asReadonly();
  }

  constructor() {
    effect(() => {
      if (this._authService.isAuthenticated$()) {
        this.updateCurrentWeatherData();
      }
    });

    setInterval(() => {
      if (this._authService.isAuthenticated$()) {
        this.updateCurrentWeatherData();
      }
    }, 60000)
  }

  loadCurrentWeatherData(lat: number, lon: number) {
    this.getCurrentWeather(lat, lon)
      .pipe(
        take(1),
        tap((value) => this._currentWeather$.set(value.data[0])),
      )
      .subscribe()
  }

  private updateCurrentWeatherData() {
    navigator.geolocation.getCurrentPosition(
      ({ coords }) => this.loadCurrentWeatherData(coords.latitude, coords.longitude),
      (() => this._doesLocationAllowed$.set(false))
    );
  }

  private getCurrentWeather(lat: number, lon: number) {
    return this._httpClient.get<CurrentWeatherApiResponse>('https://api.weatherbit.io/v2.0/current', {
      params: {
        lat,
        lon,
        key: 'api-key',
        lang: 'uk'
      }
    })
  }
}
