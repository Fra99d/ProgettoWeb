import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class CredentialsInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (req.url.startsWith('http://localhost:8080')) {
      let headers = req.headers;
      const token = this.getCookie('XSRF-TOKEN');
      if (token && !headers.has('X-XSRF-TOKEN')) {
        headers = headers.set('X-XSRF-TOKEN', token);
      }
      return next.handle(req.clone({ withCredentials: true, headers }));
    }
    return next.handle(req);
  }

  private getCookie(name: string): string | null {
    const match = document.cookie.match(new RegExp(`(?:^|; )${name}=([^;]*)`));
    return match ? decodeURIComponent(match[1]) : null;
  }
}
