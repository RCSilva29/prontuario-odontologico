import { inject } from '@angular/core';
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const usuario = authService.obterUsuario();

  let request = req;

  if (usuario?.token) {
    request = req.clone({
      setHeaders: {
        Authorization: `Bearer ${usuario.token}`
      }
    });
  }

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {

      const rotaAtual = router.url;

      const rotaPublica =
        rotaAtual.startsWith('/login') ||
        rotaAtual.startsWith('/minha-senha?obrigatoria=true');

      if ((error.status === 401 || error.status === 403) && !rotaPublica) {
        authService.logout();
        router.navigate(['/login']);
      }

      return throwError(() => error);
    })
  );
};