<?php
// Database setup script to ensure admin roles are properly configured
header('Content-Type: application/json');

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
    echo "✅ Database connected successfully\n";
    
    // Check if role column exists in accounts table
    $checkColumn = $pdo->query("SHOW COLUMNS FROM accounts LIKE 'role'");
    $roleColumnExists = $checkColumn->rowCount() > 0;
    
    if (!$roleColumnExists) {
        echo "⚠️ Role column doesn't exist. Adding role column to accounts table...\n";
        
        // Add role column to accounts table
        $pdo->exec("ALTER TABLE accounts ADD COLUMN role VARCHAR(20) DEFAULT 'user'");
        echo "✅ Role column added successfully\n";
    } else {
        echo "✅ Role column already exists\n";
    }
    
    // Set up admin users
    $adminPhones = ['0123456789', '0987654321', '0999999999', '0000000000'];
    $adminAccountIds = [1, 2]; // Add specific admin account IDs
    
    // Update users by phone number
    foreach ($adminPhones as $phone) {
        $stmt = $pdo->prepare("UPDATE accounts SET role = 'admin' WHERE phone_number = ?");
        $stmt->execute([$phone]);
        if ($stmt->rowCount() > 0) {
            echo "✅ Set admin role for phone: $phone\n";
        }
    }
    
    // Update users by account ID
    foreach ($adminAccountIds as $accountId) {
        $stmt = $pdo->prepare("UPDATE accounts SET role = 'admin' WHERE account_id = ?");
        $stmt->execute([$accountId]);
        if ($stmt->rowCount() > 0) {
            echo "✅ Set admin role for account ID: $accountId\n";
        }
    }
    
    // Create a test admin account if it doesn't exist
    $testAdminPhone = '0123456789';
    $checkAdmin = $pdo->prepare("SELECT account_id FROM accounts WHERE phone_number = ?");
    $checkAdmin->execute([$testAdminPhone]);
    
    if ($checkAdmin->rowCount() == 0) {
        echo "⚠️ Test admin account doesn't exist. Creating test admin account...\n";
        
        $hashedPassword = password_hash('123456', PASSWORD_BCRYPT);
        $createAdmin = $pdo->prepare("INSERT INTO accounts (phone_number, username, password, role) VALUES (?, ?, ?, ?)");
        $createAdmin->execute([$testAdminPhone, 'Admin Test', $hashedPassword, 'admin']);
        
        echo "✅ Test admin account created:\n";
        echo "   Phone: $testAdminPhone\n";
        echo "   Password: 123456\n";
        echo "   Role: admin\n";
    } else {
        echo "✅ Test admin account already exists\n";
    }
    
    // Show all admin users
    echo "\n📊 Current Admin Users:\n";
    $adminUsers = $pdo->query("SELECT account_id, username, phone_number, role FROM accounts WHERE role = 'admin'");
    while ($admin = $adminUsers->fetch()) {
        echo "   ID: {$admin['account_id']}, Username: {$admin['username']}, Phone: {$admin['phone_number']}, Role: {$admin['role']}\n";
    }
    
    echo "\n✅ Admin role setup completed successfully!\n";
    echo "\n🔧 You can now login with any of the admin accounts to access the admin dashboard.\n";
    
} catch (\PDOException $e) {
    echo "❌ Database error: " . $e->getMessage() . "\n";
    http_response_code(500);
}
?>
