<?php
header('Content-Type: application/json');
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Content-Type");
header("Access-Control-Allow-Methods: GET, OPTIONS");

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

$host = 'localhost';
$db   = 'whalexe';
$user = 'root';
$pass = '';
$charset = 'utf8mb4';

$dsn = "mysql:host=$host;dbname=$db;charset=$charset";
$options = [
    PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
];

try {
    $pdo = new PDO($dsn, $user, $pass, $options);
} catch (\PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database connection failed']);
    exit;
}

// Handle GET request for admin statistics
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    try {
        // Initialize default values
        $totalBookings = 0;
        $totalCars = 0;
        $todayBookings = 0;
        $weekBookings = 0;
        $monthBookings = 0;
        $cancelRate = 0.0;
        $successRate = 0.0;

        // Get total bookings (check if bookings table exists)
        try {
            $stmt = $pdo->prepare("SELECT COUNT(*) as total FROM bookings");
            $stmt->execute();
            $totalBookings = $stmt->fetchColumn();
        } catch (Exception $e) {
            // Table might not exist, use default value
            $totalBookings = 0;
        }

        // Get total cars
        try {
            $stmt = $pdo->prepare("SELECT COUNT(*) as total FROM vehicles");
            $stmt->execute();
            $totalCars = $stmt->fetchColumn();
        } catch (Exception $e) {
            // Table might not exist, use default value
            $totalCars = 0;
        }

        // Get today's bookings (using proper date fields)
        try {
            $stmt = $pdo->prepare("SELECT COUNT(*) as total FROM bookings WHERE DATE(COALESCE(pickup_date, created_at, NOW())) = CURDATE()");
            $stmt->execute();
            $todayBookings = $stmt->fetchColumn();
        } catch (Exception $e) {
            $todayBookings = 0;
        }

        // Get this week's bookings
        try {
            $stmt = $pdo->prepare("SELECT COUNT(*) as total FROM bookings WHERE YEARWEEK(COALESCE(pickup_date, created_at, NOW()), 1) = YEARWEEK(CURDATE(), 1)");
            $stmt->execute();
            $weekBookings = $stmt->fetchColumn();
        } catch (Exception $e) {
            $weekBookings = 0;
        }

        // Get this month's bookings
        try {
            $stmt = $pdo->prepare("SELECT COUNT(*) as total FROM bookings WHERE YEAR(COALESCE(pickup_date, created_at, NOW())) = YEAR(CURDATE()) AND MONTH(COALESCE(pickup_date, created_at, NOW())) = MONTH(CURDATE())");
            $stmt->execute();
            $monthBookings = $stmt->fetchColumn();
        } catch (Exception $e) {
            $monthBookings = 0;
        }

        // Calculate cancel rate and success rate (adjust status values as needed)
        try {
            $stmt = $pdo->prepare("SELECT 
                COUNT(CASE WHEN status IN ('cancelled', 'canceled', 'cancel') THEN 1 END) as cancelled,
                COUNT(CASE WHEN status IN ('completed', 'finished', 'returned', 'complete') THEN 1 END) as completed,
                COUNT(*) as total
                FROM bookings");
            $stmt->execute();
            $statusStats = $stmt->fetch();

            $cancelRate = $statusStats['total'] > 0 ? ($statusStats['cancelled'] / $statusStats['total']) * 100 : 0;
            $successRate = $statusStats['total'] > 0 ? ($statusStats['completed'] / $statusStats['total']) * 100 : 0;
        } catch (Exception $e) {
            $cancelRate = 0.0;
            $successRate = 0.0;
        }

        $stats = [
            'total_bookings' => (int)$totalBookings,
            'total_cars' => (int)$totalCars,
            'today_bookings' => (int)$todayBookings,
            'week_bookings' => (int)$weekBookings,
            'month_bookings' => (int)$monthBookings,
            'cancel_rate' => round($cancelRate, 1),
            'success_rate' => round($successRate, 1)
        ];

        echo json_encode(['success' => true, 'stats' => $stats]);

    } catch (\PDOException $e) {
        // Return default values with error info for debugging
        $stats = [
            'total_bookings' => 0,
            'total_cars' => 0,
            'today_bookings' => 0,
            'week_bookings' => 0,
            'month_bookings' => 0,
            'cancel_rate' => 0.0,
            'success_rate' => 0.0,
            'debug_error' => $e->getMessage()
        ];

        echo json_encode(['success' => true, 'stats' => $stats]);
    } catch (Exception $e) {
        // Return default values with error info for debugging
        $stats = [
            'total_bookings' => 0,
            'total_cars' => 0,
            'today_bookings' => 0,
            'week_bookings' => 0,
            'month_bookings' => 0,
            'cancel_rate' => 0.0,
            'success_rate' => 0.0,
            'debug_error' => $e->getMessage()
        ];

        echo json_encode(['success' => true, 'stats' => $stats]);
    }
} else {
    http_response_code(405);
    echo json_encode(['error' => 'Method not allowed']);
}
?>
