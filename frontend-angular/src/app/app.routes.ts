import { Routes } from '@angular/router';

import { Login } from './pages/login/login';
import { Pacientes } from './pages/pacientes/pacientes';
import { PacienteForm } from './pages/paciente-form/paciente-form';
import { PacienteDetalhe } from './pages/paciente-detalhe/paciente-detalhe';
import { authGuard } from './guards/auth.guard';
import { Usuarios } from './pages/usuarios/usuarios';
import { MinhaSenha } from './pages/minha-senha/minha-senha';
import { adminGuard } from './guards/admin.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: Login
  },
  {
    path: 'pacientes',
    component: Pacientes,
    canActivate: [authGuard]
  },
  {
    path: 'pacientes/novo',
    component: PacienteForm,
    canActivate: [authGuard]
  },
  {
    path: 'pacientes/:id/editar',
    component: PacienteForm,
    canActivate: [authGuard]
  },
  {
    path: 'pacientes/:id',
    component: PacienteDetalhe,
    canActivate: [authGuard]
  },
  {
    path: 'usuarios',
    component: Usuarios,
    canActivate: [authGuard, adminGuard]
  },
  {
    path: 'minha-senha',
    component: MinhaSenha
  }
];