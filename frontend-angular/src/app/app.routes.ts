import { Routes } from '@angular/router';
import { Pacientes } from './pages/pacientes/pacientes';
import { PacienteForm } from './pages/paciente-form/paciente-form';
import { PacienteDetalhe } from './pages/paciente-detalhe/paciente-detalhe';

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
  },
  {
    path: 'pacientes/:id',
    component: PacienteDetalhe
  }
];