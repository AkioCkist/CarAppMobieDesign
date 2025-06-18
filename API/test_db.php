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
    
    $result = [
        'database_connected' => true,
        'tables' => []
    ];
    
    // Check if tables exist
    $tables = ['accounts', 'account_roles', 'bookings', 'favorites', 'roles', 'vehicles', 'vehicle_amenities', 'vehicle_amenity_mapping', 'vehicle_images'];
    
    foreach ($tables as $table) {
        try {
            $stmt = $pdo->prepare("SELECT COUNT(*) as count FROM $table");
            $stmt->execute();
            $count = $stmt->fetchColumn();
            $result['tables'][$table] = [
                'exists' => true,
                'count' => (int)$count
            ];
        } catch (Exception $e) {
            $result['tables'][$table] = [
                'exists' => false,
                'error' => $e->getMessage()
            ];
        }
    }
    
    // Check for admin users
    try {
        $stmt = $pdo->prepare("
            SELECT a.account_id, a.username, a.phone_number, r.role_name
            FROM accounts a
            LEFT JOIN account_roles ar ON a.account_id = ar.account_id
            LEFT JOIN roles r ON ar.role_id = r.role_id
            WHERE r.role_name = 'admin'
        ");
        $stmt->execute();
        $admins = $stmt->fetchAll();
        $result['admin_users'] = $admins;
    } catch (Exception $e) {
        $result['admin_users_error'] = $e->getMessage();
    }
    
    echo json_encode($result, JSON_PRETTY_PRINT);
    
} catch (\PDOException $e) {
    echo json_encode([
        'database_connected' => false,
        'error' => $e->getMessage()
    ], JSON_PRETTY_PRINT);
}
?>
