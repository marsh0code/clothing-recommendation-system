import { inject, Pipe, PipeTransform } from '@angular/core';
import { map, Observable, of, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Pipe({
  name: 'getSecuredImage',
  standalone: true,
})
export class GetSecuredImagePipe implements PipeTransform {
  private _httpClient = inject(HttpClient);
  private _sanitizer = inject(DomSanitizer);

  transform(url: string): Observable<SafeUrl> {
    return this._httpClient.get(url, { responseType: 'blob' })
      .pipe(
        map((image => this._sanitizer.bypassSecurityTrustUrl(URL.createObjectURL(image)))),
      )
  }
}
