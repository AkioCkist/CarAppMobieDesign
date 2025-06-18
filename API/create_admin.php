<?php
// This script creates an admin account for testing
// Run this once to create an admin user in your database

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
    
    // Create admin role if it doesn't exist
    $stmt = $pdo->prepare("INSERT IGNORE INTO roles (role_id, role_name) VALUES (1, 'admin'), (2, 'user')");
    $stmt->execute();
    
    // Create admin account
    $adminPhone = '0123456789';
    $adminPassword = password_hash('admin123', PASSWORD_DEFAULT);
    $adminUsername = 'Admin';
    
    // Check if admin account already exists
    $stmt = $pdo->prepare("SELECT account_id FROM accounts WHERE phone_number = ?");
    $stmt->execute([$adminPhone]);
    $existingAdmin = $stmt->fetch();
    
    if (!$existingAdmin) {
        // Create admin account
        $stmt = $pdo->prepare("INSERT INTO accounts (username, phone_number, password, created_at) VALUES (?, ?, ?, NOW())");
        $stmt->execute([$adminUsername, $adminPhone, $adminPassword]);
        $adminAccountId = $pdo->lastInsertId();
        
        // Assign admin role
        $stmt = $pdo->prepare("INSERT INTO account_roles (account_id, role_id) VALUES (?, 1)");
        $stmt->execute([$adminAccountId]);
        
        echo "Admin account created successfully!\n";
        echo "Phone: 0123456789\n";
        echo "Password: admin123\n";
    } else {
        $adminAccountId = $existingAdmin['account_id'];
        
        // Make sure this account has admin role
        $stmt = $pdo->prepare("INSERT IGNORE INTO account_roles (account_id, role_id) VALUES (?, 1)");
        $stmt->execute([$adminAccountId]);
        
        echo "Admin account already exists!\n";
        echo "Phone: 0123456789\n";
        echo "Make sure password is: admin123\n";
    }
    
    // Create some sample data for testing
    
    // Sample vehicles
    $sampleVehicles = [
        ['name' => 'Toyota Camry 2024', 'brand' => 'Toyota', 'model' => 'Camry', 'status' => 'available'],
        ['name' => 'Honda Civic 2024', 'brand' => 'Honda', 'model' => 'Civic', 'status' => 'available'],
        ['name' => 'BMW X5 2024', 'brand' => 'BMW', 'model' => 'X5', 'status' => 'rented'],
        ['name' => 'Mercedes C-Class 2024', 'brand' => 'Mercedes', 'model' => 'C-Class', 'status' => 'maintenance'],
    ];
    
    foreach ($sampleVehicles as $vehicle) {
        $stmt = $pdo->prepare("INSERT IGNORE INTO vehicles (name, brand, model, status, created_at) VALUES (?, ?, ?, ?, NOW())");
        $stmt->execute([$vehicle['name'], $vehicle['brand'], $vehicle['model'], $vehicle['status']]);
    }
    
    echo "Sample vehicles created!\n";
    
    // Create a regular user for testing
    $userPhone = '0987654321';
    $userPassword = password_hash('user123', PASSWORD_DEFAULT);
    $username = 'TestUser';
    
    $stmt = $pdo->prepare("SELECT account_id FROM accounts WHERE phone_number = ?");
    $stmt->execute([$userPhone]);
    $existingUser = $stmt->fetch();
    
    if (!$existingUser) {
        $stmt = $pdo->prepare("INSERT INTO accounts (username, phone_number, password, created_at) VALUES (?, ?, ?, NOW())");
        $stmt->execute([$username, $userPhone, $userPassword]);
        $userAccountId = $pdo->lastInsertId();
        
        // Assign user role
        $stmt = $pdo->prepare("INSERT INTO account_roles (account_id, role_id) VALUES (?, 2)");
        $stmt->execute([$userAccountId]);
        
        echo "Test user created!\n";
        echo "Phone: 0987654321\n";
        echo "Password: user123\n";
    }
    
    // Create sample bookings
    $stmt = $pdo->prepare("SELECT vehicle_id FROM vehicles LIMIT 2");
    $stmt->execute();
    $vehicles = $stmt->fetchAll();
    
    $stmt = $pdo->prepare("SELECT account_id FROM accounts WHERE phone_number = ?");
    $stmt->execute([$userPhone]);
    $user = $stmt->fetch();
    
    if ($user && !empty($vehicles)) {
        $userId = $user['account_id'];
        
        // Sample booking statuses
        $bookingStatuses = ['completed', 'ongoing', 'cancelled'];
        
        foreach ($vehicles as $index => $vehicle) {
            $status = $bookingStatuses[$index % count($bookingStatuses)];
            $pickupDate = date('Y-m-d', strtotime("-" . ($index + 1) . " days"));
            $returnDate = date('Y-m-d', strtotime("+" . ($index + 3) . " days"));
            
            $stmt = $pdo->prepare("INSERT IGNORE INTO bookings (renter_id, vehicle_id, pickup_date, return_date, status, created_at) VALUES (?, ?, ?, ?, ?, NOW())");
            $stmt->execute([$userId, $vehicle['vehicle_id'], $pickupDate, $returnDate, $status]);
        }
        
        echo "Sample bookings created!\n";
    }
    
    echo "\n=== SETUP COMPLETE ===\n";
    echo "Admin login: 0123456789 / admin123\n";
    echo "User login: 0987654321 / user123\n";
    
} catch (\PDOException $e) {
    echo "Error: " . $e->getMessage() . "\n";
}
?>
