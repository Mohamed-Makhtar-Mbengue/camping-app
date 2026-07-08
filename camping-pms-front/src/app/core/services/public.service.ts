import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Accommodation, PageResponse } from './accommodation.service';

export interface PublicBookingRequest {
  accommodationId: string;
  startDate: string;
  endDate: string;
  adults: number;
  children: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
}

export interface BookingConfirmation {
  bookingId: string;
  totalPrice: number;
  accommodation: string;
  startDate: string;
  endDate: string;
  nights: number;
  customerEmail: string;
  acsiApplied?: boolean;
  acsiDiscount?: number;
}

export interface AcsiCheckResult {
  eligible: boolean;
  acsiPrice: number;
}

@Injectable({ providedIn: 'root' })
export class PublicService {

  private apiUrl = `${environment.apiUrl}/public`;

  constructor(private http: HttpClient) {}

  getAccommodations(page = 0, size = 9): Observable<PageResponse<Accommodation>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);
    return this.http.get<PageResponse<Accommodation>>(`${this.apiUrl}/accommodations`, { params });
  }

  getAccommodation(id: string): Observable<Accommodation> {
    return this.http.get<Accommodation>(`${this.apiUrl}/accommodations/${id}`);
  }

  checkAvailability(id: string, startDate: string, endDate: string): Observable<{ available: boolean }> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<{ available: boolean }>(
      `${this.apiUrl}/accommodations/${id}/availability`, { params }
    );
  }

  createBooking(request: PublicBookingRequest): Observable<BookingConfirmation> {
    return this.http.post<BookingConfirmation>(`${this.apiUrl}/bookings`, request);
  }

  checkAcsiEligibility(startDate: string, endDate: string): Observable<AcsiCheckResult> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<AcsiCheckResult>(`${this.apiUrl}/acsi/check`, { params });
  }

  downloadBonEchange(bookingId: string): Observable<Blob> {
  return this.http.get(
    `${this.apiUrl}/bookings/${bookingId}/pdf`,
    { responseType: 'blob' }
  );
  }
}