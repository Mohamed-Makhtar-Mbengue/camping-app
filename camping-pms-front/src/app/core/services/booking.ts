import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Booking {
  id: string;
  accommodation: {
    id: string;
    name: string;
    type: string;
    basePrice: number;
  };
  customer: {
    id: string;
    firstName: string;
    lastName: string;
    email: string;
  };
  startDate: string;
  endDate: string;
  adults: number;
  children: number;
  totalPrice: number;
  status: string;
  depositAmount: number;
  depositStatus: string;
  depositReturnedDate: string | null;
  depositDeduction: number;
  depositDeductionReason: string | null;
}

export interface CreateBookingRequest {
  accommodationId: string;
  startDate: string;
  endDate: string;
  adults: number;
  children: number;
}

@Injectable({ providedIn: 'root' })
export class BookingService {

  private apiUrl = `${environment.apiUrl}/api/bookings`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Booking[]> {
    return this.http.get<Booking[]>(this.apiUrl);
  }

  getMine(): Observable<Booking[]> {
    return this.http.get<Booking[]>(`${this.apiUrl}/my`);
  }

  create(request: CreateBookingRequest): Observable<Booking> {
    return this.http.post<Booking>(this.apiUrl, request);
  }

  updateStatus(id: string, status: string): Observable<Booking> {
    return this.http.patch<Booking>(`${this.apiUrl}/${id}/status?status=${status}`, {});
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  holdDeposit(id: string, amount: number): Observable<Booking> {
    return this.http.patch<Booking>(`${this.apiUrl}/${id}/deposit/hold?amount=${amount}`, {});
  }

  returnDeposit(id: string): Observable<Booking> {
    return this.http.patch<Booking>(`${this.apiUrl}/${id}/deposit/return`, {});
  }

  partialRetainDeposit(id: string, deduction: number, reason: string): Observable<Booking> {
    return this.http.patch<Booking>(
      `${this.apiUrl}/${id}/deposit/partial-retain?deduction=${deduction}&reason=${encodeURIComponent(reason)}`, {}
    );
  }
}