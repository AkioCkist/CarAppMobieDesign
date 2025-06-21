<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

// Debug info
$debug = true; // Set to false in production
if ($debug) {
    error_reporting(E_ALL);
    ini_set('display_errors', 1);
}

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

// Database configuration
$host = 'localhost';
$dbname = 'test';
$username = 'root';
$password = '';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch(PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database connection failed: ' . $e->getMessage()]);
    exit();
}

// Get request method and path
$method = $_SERVER['REQUEST_METHOD'];
$path = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
$path_parts = explode('/', trim($path, '/'));

// Extract booking ID if provided
$booking_id = isset($path_parts[2]) ? intval($path_parts[2]) : null;

// Debug request
if ($debug) {
    $debug_info = [
        'method' => $method,
        'path' => $path,
        'query_params' => $_GET,
        'booking_id' => $booking_id
    ];
    error_log("API Request: " . json_encode($debug_info));
}

// Route requests
switch ($method) {
    case 'GET':
        if ($booking_id) {
            getBookingById($pdo, $booking_id, $debug);
        } else {
            getAllBookings($pdo, $debug);
        }
        break;
    
    case 'POST':
        createBooking($pdo);
        break;
    
    case 'PUT':
        if ($booking_id) {
            updateBooking($pdo, $booking_id);
        } else {
            http_response_code(400);
            echo json_encode(['error' => 'Booking ID is required for update']);
        }
        break;
    
    case 'DELETE':
        if ($booking_id) {
            deleteBooking($pdo, $booking_id);
        } else {
            http_response_code(400);
            echo json_encode(['error' => 'Booking ID is required for deletion']);
        }
        break;
    
    default:
        http_response_code(405);
        echo json_encode(['error' => 'Method not allowed']);
        break;
}

// Function to get all bookings with detailed information
function getAllBookings($pdo, $debug = false) {
    try {
        // Get query parameters for filtering
        $status = $_GET['status'] ?? null;
        $renter_id = $_GET['renter_id'] ?? null;
        $vehicle_id = $_GET['vehicle_id'] ?? null;
        $limit = $_GET['limit'] ?? 50;
        $offset = $_GET['offset'] ?? 0;
        
        $sql = "SELECT 
                    b.*,
                    v.name as vehicle_name,
                    v.vehicle_type,
                    v.base_price,
                    a.username as renter_username,
                    a.phone_number as renter_phone,
                    lessor.username as lessor_username,
                    lessor.phone_number as lessor_phone
                FROM bookings b
                JOIN vehicles v ON b.vehicle_id = v.vehicle_id
                JOIN accounts a ON b.renter_id = a.account_id
                JOIN accounts lessor ON v.lessor_id = lessor.account_id
                WHERE 1=1";
        
        $params = [];
        
        if ($status) {
            $sql .= " AND b.status = :status";
            $params['status'] = $status;
        }
        
        if ($renter_id) {
            $sql .= " AND b.renter_id = :renter_id";
            $params['renter_id'] = $renter_id;
        }
        
        if ($vehicle_id) {
            $sql .= " AND b.vehicle_id = :vehicle_id";
            $params['vehicle_id'] = $vehicle_id;
        }
        
        $sql .= " ORDER BY b.created_at DESC LIMIT :limit OFFSET :offset";

        if ($debug) {
            error_log("SQL Query: " . $sql);
            error_log("Parameters: " . json_encode($params));
        }

        $stmt = $pdo->prepare($sql);

        foreach ($params as $key => $value) {
            if ($key === 'limit' || $key === 'offset') {
                $stmt->bindValue(":$key", intval($value), PDO::PARAM_INT);
            } else {
                $stmt->bindValue(":$key", $value);
            }
        }

        // Bind these parameters separately since they're always present
        $stmt->bindValue(":limit", intval($limit), PDO::PARAM_INT);
        $stmt->bindValue(":offset", intval($offset), PDO::PARAM_INT);

        $stmt->execute();
        
        $bookings = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        // Debug
        if ($debug) {
            error_log("Found " . count($bookings) . " bookings");
            if ($renter_id) {
                error_log("Bookings for renter_id=$renter_id: " . json_encode(array_column($bookings, 'booking_id')));
            }
        }

        // Get total count
        $countSql = "SELECT COUNT(*) as total FROM bookings b WHERE 1=1";
        $countParams = [];
        
        if ($status) {
            $countSql .= " AND b.status = :status";
            $countParams['status'] = $status;
        }
        
        if ($renter_id) {
            $countSql .= " AND b.renter_id = :renter_id";
            $countParams['renter_id'] = $renter_id;
        }
        
        if ($vehicle_id) {
            $countSql .= " AND b.vehicle_id = :vehicle_id";
            $countParams['vehicle_id'] = $vehicle_id;
        }
        
        $countStmt = $pdo->prepare($countSql);
        $countStmt->execute($countParams);
        $total = $countStmt->fetch(PDO::FETCH_ASSOC)['total'];
        
        $response = [
            'success' => true,
            'data' => $bookings,
            'total' => intval($total),
            'limit' => intval($limit),
            'offset' => intval($offset)
        ];

        // Debug the response
        if ($debug) {
            error_log("Response summary: success=true, total=" . $total . ", returned=" . count($bookings));
        }

        echo json_encode($response);

    } catch(PDOException $e) {
        http_response_code(500);
        $errorMsg = 'Database error: ' . $e->getMessage();
        if ($debug) {
            error_log("ERROR: " . $errorMsg);
        }
        echo json_encode(['error' => $errorMsg]);
    }
}

// Function to get a specific booking by ID
function getBookingById($pdo, $booking_id, $debug = false) {
    try {
        $sql = "SELECT 
                    b.*,
                    v.name as vehicle_name,
                    v.vehicle_type,
                    v.base_price,
                    v.rating,
                    v.location as vehicle_location,
                    a.username as renter_username,
                    a.phone_number as renter_phone,
                    lessor.username as lessor_username,
                    lessor.phone_number as lessor_phone
                FROM bookings b
                JOIN vehicles v ON b.vehicle_id = v.vehicle_id
                JOIN accounts a ON b.renter_id = a.account_id
                JOIN accounts lessor ON v.lessor_id = lessor.account_id
                WHERE b.booking_id = :booking_id";
        
        if ($debug) {
            error_log("Getting booking by ID: " . $booking_id);
        }

        $stmt = $pdo->prepare($sql);
        $stmt->bindParam(':booking_id', $booking_id, PDO::PARAM_INT);
        $stmt->execute();
        
        $booking = $stmt->fetch(PDO::FETCH_ASSOC);
        
        if ($booking) {
            echo json_encode([
                'success' => true,
                'data' => $booking
            ]);
        } else {
            http_response_code(404);
            echo json_encode(['error' => 'Booking not found']);
        }
        
    } catch(PDOException $e) {
        http_response_code(500);
        echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
    }
}

// Function to create a new booking
function createBooking($pdo) {
    try {
        $input = json_decode(file_get_contents('php://input'), true);
        
        // Validate required fields
        $required_fields = ['vehicle_id', 'renter_id', 'pickup_date', 'pickup_time', 
                           'return_date', 'return_time', 'pickup_location', 
                           'return_location', 'total_price', 'final_price'];
        
        foreach ($required_fields as $field) {
            if (!isset($input[$field]) || empty($input[$field])) {
                http_response_code(400);
                echo json_encode(['error' => "Field '$field' is required"]);
                return;
            }
        }
        
        $sql = "INSERT INTO bookings (
                    vehicle_id, renter_id, pickup_date, pickup_time, 
                    return_date, return_time, pickup_location, return_location,
                    total_price, discount_applied, final_price, status
                ) VALUES (
                    :vehicle_id, :renter_id, :pickup_date, :pickup_time,
                    :return_date, :return_time, :pickup_location, :return_location,
                    :total_price, :discount_applied, :final_price, :status
                )";
        
        $stmt = $pdo->prepare($sql);
        $stmt->execute([
            'vehicle_id' => $input['vehicle_id'],
            'renter_id' => $input['renter_id'],
            'pickup_date' => $input['pickup_date'],
            'pickup_time' => $input['pickup_time'],
            'return_date' => $input['return_date'],
            'return_time' => $input['return_time'],
            'pickup_location' => $input['pickup_location'],
            'return_location' => $input['return_location'],
            'total_price' => $input['total_price'],
            'discount_applied' => $input['discount_applied'] ?? 0.00,
            'final_price' => $input['final_price'],
            'status' => $input['status'] ?? 'pending'
        ]);
        
        $booking_id = $pdo->lastInsertId();
        
        echo json_encode([
            'success' => true,
            'message' => 'Booking created successfully',
            'booking_id' => intval($booking_id)
        ]);
        
    } catch(PDOException $e) {
        http_response_code(500);
        echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
    }
}

// Function to update a booking
function updateBooking($pdo, $booking_id) {
    try {
        $input = json_decode(file_get_contents('php://input'), true);
        
        // Check if booking exists
        $checkSql = "SELECT booking_id FROM bookings WHERE booking_id = :booking_id";
        $checkStmt = $pdo->prepare($checkSql);
        $checkStmt->bindParam(':booking_id', $booking_id, PDO::PARAM_INT);
        $checkStmt->execute();
        
        if (!$checkStmt->fetch()) {
            http_response_code(404);
            echo json_encode(['error' => 'Booking not found']);
            return;
        }
        
        // Build dynamic update query
        $updateFields = [];
        $params = ['booking_id' => $booking_id];
        
        $allowedFields = ['pickup_date', 'pickup_time', 'return_date', 'return_time',
                         'pickup_location', 'return_location', 'total_price', 
                         'discount_applied', 'final_price', 'status'];
        
        foreach ($allowedFields as $field) {
            if (isset($input[$field])) {
                $updateFields[] = "$field = :$field";
                $params[$field] = $input[$field];
            }
        }
        
        if (empty($updateFields)) {
            http_response_code(400);
            echo json_encode(['error' => 'No valid fields to update']);
            return;
        }
        
        $sql = "UPDATE bookings SET " . implode(', ', $updateFields) . 
               " WHERE booking_id = :booking_id";
        
        $stmt = $pdo->prepare($sql);
        $stmt->execute($params);
        
        echo json_encode([
            'success' => true,
            'message' => 'Booking updated successfully'
        ]);
        
    } catch(PDOException $e) {
        http_response_code(500);
        echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
    }
}

// Function to delete a booking
function deleteBooking($pdo, $booking_id) {
    try {
        $sql = "DELETE FROM bookings WHERE booking_id = :booking_id";
        $stmt = $pdo->prepare($sql);
        $stmt->bindParam(':booking_id', $booking_id, PDO::PARAM_INT);
        $stmt->execute();
        
        if ($stmt->rowCount() > 0) {
            echo json_encode([
                'success' => true,
                'message' => 'Booking deleted successfully'
            ]);
        } else {
            http_response_code(404);
            echo json_encode(['error' => 'Booking not found']);
        }
        
    } catch(PDOException $e) {
        http_response_code(500);
        echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
    }
}
?>