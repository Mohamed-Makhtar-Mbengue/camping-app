import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Accommodation {
  id: string;
  name: string;
  type: string;
  capacity: number;
  basePrice: number;
  description: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({ providedIn: 'root' })
export class AccommodationService {

  private apiUrl = `${environment.apiUrl}/api/accommodations`;

  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 10, sortBy = 'name'): Observable<PageResponse<Accommodation>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sortBy', sortBy);
    return this.http.get<PageResponse<Accommodation>>(this.apiUrl, { params });
  }

  getById(id: string): Observable<Accommodation> {
    return this.http.get<Accommodation>(`${this.apiUrl}/${id}`);
  }

  create(accommodation: Partial<Accommodation>): Observable<Accommodation> {
    return this.http.post<Accommodation>(this.apiUrl, accommodation);
  }

  update(id: string, accommodation: Partial<Accommodation>): Observable<Accommodation> {
    return this.http.put<Accommodation>(`${this.apiUrl}/${id}`, accommodation);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}