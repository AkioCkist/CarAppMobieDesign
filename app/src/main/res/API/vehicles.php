<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

include_once 'amenity_vehicles.php';

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Database connection
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

// Initialize Vehicle class
$vehicle = new Vehicle($pdo);

$request_method = $_SERVER["REQUEST_METHOD"];

switch($request_method) {
    case 'GET':
        if (!empty($_GET["id"])) {
            $id = intval($_GET["id"]);
            get_vehicle_detail($vehicle, $id);
        } else {
            get_vehicles($vehicle);
        }
        break;
    case 'POST':
        if (isset($_GET['action']) && $_GET['action'] == 'toggle_favorite') {
            toggle_favorite($vehicle);
        }
        break;
    default:
        header("HTTP/1.0 405 Method Not Allowed");
        break;
}

function get_vehicles($vehicle) {
    // Xử lý tham số query
    $limit = isset($_GET['limit']) ? intval($_GET['limit']) : null;
    $offset = isset($_GET['offset']) ? intval($_GET['offset']) : null;
    $search = isset($_GET['search']) ? $_GET['search'] : null;
    $type = isset($_GET['type']) ? $_GET['type'] : null;
    $location = isset($_GET['location']) ? $_GET['location'] : null;
    $favorites_only = isset($_GET['favorites']) && $_GET['favorites'] == 'true';

    try {
        if ($favorites_only) {
            $stmt = $vehicle->getFavoriteVehicles();
        } elseif ($search || $type || $location) {
            $stmt = $vehicle->searchVehicles($search, $type, $location);
        } else {
            $stmt = $vehicle->getAllVehicles($limit, $offset);
        }

        $num = $stmt->rowCount();

        if ($num > 0) {
            $vehicles_arr = array();
            $vehicles_arr["records"] = array();
            $vehicles_arr["total"] = $num;

            while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
                extract($row);

                // Lấy hình ảnh của xe
                $images_stmt = $vehicle->getVehicleImages($id);
                $images = array();
                while ($img_row = $images_stmt->fetch(PDO::FETCH_ASSOC)) {
                    $images[] = array(
                        "url" => $img_row["image_url"],
                        "is_primary" => $img_row["is_primary"] == 1,
                        "display_order" => $img_row["display_order"]
                    );
                }

                // Lấy tiện nghi của xe
                $amenities_stmt = $vehicle->getVehicleAmenities($id);
                $amenities = array();
                while ($amenity_row = $amenities_stmt->fetch(PDO::FETCH_ASSOC)) {
                    $amenities[] = array(
                        "name" => $amenity_row["amenity_name"],
                        "icon" => $amenity_row["amenity_icon"],
                        "description" => $amenity_row["description"]
                    );
                }

                // Format giá tiền
                $price_display = number_format($base_price / 1000, 0) . "K/ngày";
                
                // Tạo old price giả (bạn có thể điều chỉnh logic này)
                $old_price = null;
                $price_discount = null;
                if ($rating >= 4.5) {
                    $old_price = $base_price * 1.2;
                    $price_discount = "Giảm 20%";
                }

                $vehicle_item = array(
                    "id" => $id,
                    "name" => $name,
                    "rating" => floatval($rating),
                    "trips" => intval($trips),
                    "location" => $location,
                    "transmission" => $transmission,
                    "seats" => intval($seats),
                    "fuel" => $fuel,
                    "base_price" => floatval($base_price),
                    "priceDisplay" => $price_display,
                    "oldPrice" => $old_price,
                    "priceDiscount" => $price_discount,
                    "pricePer" => "ngày",
                    "vehicle_type" => $vehicle_type,
                    "description" => $description,
                    "status" => $status,
                    "is_favorite" => $is_favorite == 1,
                    "lessor_name" => $lessor_name,
                    "image" => !empty($images) ? $images[0]["url"] : "/images/default-car.jpg",
                    "images" => $images,
                    "amenities" => $amenities,
                    "created_at" => $created_at,
                    "updated_at" => $updated_at
                );

                array_push($vehicles_arr["records"], $vehicle_item);
            }

            http_response_code(200);
            echo json_encode($vehicles_arr);
        } else {
            http_response_code(200);
            echo json_encode(array(
                "message" => "Không tìm thấy xe nào.",
                "records" => array(),
                "total" => 0
            ));
        }
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(array("message" => "Lỗi server: " . $e->getMessage()));
    }
}

function get_vehicle_detail($vehicle, $id) {
    try {
        $stmt = $vehicle->getVehicleById($id);
        $num = $stmt->rowCount();

        if ($num == 1) {
            $row = $stmt->fetch(PDO::FETCH_ASSOC);
            extract($row);

            // Lấy tất cả hình ảnh
            $images_stmt = $vehicle->getVehicleImages($id);
            $images = array();
            while ($img_row = $images_stmt->fetch(PDO::FETCH_ASSOC)) {
                $images[] = array(
                    "url" => $img_row["image_url"],
                    "is_primary" => $img_row["is_primary"] == 1,
                    "display_order" => $img_row["display_order"]
                );
            }

            // Lấy tất cả tiện nghi
            $amenities_stmt = $vehicle->getVehicleAmenities($id);
            $amenities = array();
            while ($amenity_row = $amenities_stmt->fetch(PDO::FETCH_ASSOC)) {
                $amenities[] = array(
                    "name" => $amenity_row["amenity_name"],
                    "icon" => $amenity_row["amenity_icon"],
                    "description" => $amenity_row["description"]
                );
            }

            $price_display = number_format($base_price / 1000, 0) . "K/ngày";
            
            $vehicle_item = array(
                "id" => $id,
                "name" => $name,
                "rating" => floatval($rating),
                "trips" => intval($trips),
                "location" => $location,
                "transmission" => $transmission,
                "seats" => intval($seats),
                "fuel" => $fuel,
                "base_price" => floatval($base_price),
                "priceDisplay" => $price_display,
                "vehicle_type" => $vehicle_type,
                "description" => $description,
                "status" => $status,
                "is_favorite" => $is_favorite == 1,
                "lessor_name" => $lessor_name,
                "images" => $images,
                "amenities" => $amenities,
                "created_at" => $created_at,
                "updated_at" => $updated_at
            );

            http_response_code(200);
            echo json_encode($vehicle_item);
        } else {
            http_response_code(404);
            echo json_encode(array("message" => "Không tìm thấy xe."));
        }
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(array("message" => "Lỗi server: " . $e->getMessage()));
    }
}

function toggle_favorite($vehicle) {
    $data = json_decode(file_get_contents("php://input"));

    if (!empty($data->vehicle_id)) {
        $vehicle_id = $data->vehicle_id;
        $is_favorite = isset($data->is_favorite) ? $data->is_favorite : true;

        if ($vehicle->toggleFavorite($vehicle_id, $is_favorite)) {
            http_response_code(200);
            echo json_encode(array("message" => "Đã cập nhật trạng thái yêu thích."));
        } else {
            http_response_code(503);
            echo json_encode(array("message" => "Không thể cập nhật trạng thái yêu thích."));
        }
    } else {
        http_response_code(400);
        echo json_encode(array("message" => "Dữ liệu không hợp lệ."));
    }
}
?>