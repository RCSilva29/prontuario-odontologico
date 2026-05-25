import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const dados = localStorage.getItem('usuarioLogado');

  if (!dados) {
    return next(req);
  }

  const usuario = JSON.parse(dados);

  if (!usuario?.token) {
    return next(req);
  }

  const requestComToken = req.clone({
    setHeaders: {
      Authorization: `Bearer ${usuario.token}`
    }
  });

  return next(requestComToken);
};