export default function RoomCardSkeleton() {
  return (
    <div className="border rounded-lg overflow-hidden shadow-sm animate-pulse">
      <div className="h-48 bg-gray-200" />
      <div className="p-4">
        <div className="h-5 bg-gray-200 rounded w-2/3 mb-2" />
        <div className="h-4 bg-gray-200 rounded w-full mb-3" />
        <div className="flex items-center justify-between mt-3">
          <div className="h-5 bg-gray-200 rounded w-1/3" />
          <div className="h-4 bg-gray-200 rounded w-1/4" />
        </div>
      </div>
    </div>
  );
}