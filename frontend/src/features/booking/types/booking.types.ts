export interface Room {
  id: number;
  roomNumber: string;
  roomTypeName: string;
  floor: number;
  status: string;
}

export interface BookingPayload {
  checkInDate: string;
  checkOutDate: string;
  totalGuests: number;
  roomIds: number[];
}

export interface BookingResponseData {
  id: string;
  checkInDate: string;
  checkOutDate: string;
  totalGuests: number;
  status: string;
  totalAmount: number;
  roomNumbers: string[];
}