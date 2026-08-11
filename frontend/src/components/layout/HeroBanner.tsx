export default function HeroBanner() {
    return (
        <div className="relative bg-gradient-to-r from-blue-700 to-blue-500 text-white">
            <div className="max-w-6xl mx-auto px-8 py-20 text-center">
                <h1 className="text-4xl md:text-5xl font-bold mb-4">
                    Kỳ nghỉ đáng nhớ bắt đầu từ đây
                </h1>
                <p className="text-lg text-blue-100 mb-8 max-w-2xl mx-auto">
                    Đặt phòng nhanh chóng, giá tốt nhất, dịch vụ tận tâm.
                </p>
                <a href="#rooms" className="inline-block bg-white text-blue-700 font-semibold px-8 py-3 rounded-lg hover:bg-blue-50 transition">
                    Khám phá phòng ngay
                </a>
            </div>
        </div>
    );
}