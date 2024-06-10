import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  constructor(private toastr: ToastrService) {}

  showSuccessNotification(message: string): void {
    this.toastr.success(message, 'Success', {
      positionClass: 'toast-bottom-right',
      timeOut: 5000,
      enableHtml: true,
      toastClass: 'ngx-toastr success-toast',
    });
  }

  showErrorNotification(error: HttpErrorResponse): void {
    const message = error.error?.message ?? error.error ? (Object.values(error.error)[0]) ?? error.error : error.message;

    this.toastr.error(message, 'Error', {
      positionClass: 'toast-bottom-right',
      timeOut: 5000,
      enableHtml: true,
      toastClass: 'ngx-toastr error-toast',
    });
  }
}
