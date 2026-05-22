import { Routes } from '@angular/router';

import { Pacientes } from './pages/pacientes/pacientes';
import { PacienteForm } from './pages/paciente-form/paciente-form';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'pacientes',
    pathMatch: 'full'
  },
  {
    path: 'pacientes',
    component: Pacientes
  },
  {
    path: 'pacientes/novo',
    component: PacienteForm
  },
  {
    path: 'pacientes/:id/editar',
    component: PacienteForm
  }
];