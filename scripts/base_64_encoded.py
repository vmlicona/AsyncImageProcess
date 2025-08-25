import base64

def image_to_base64(file_path):
    with open(file_path, "rb") as image_file:
        return base64.b64encode(image_file.read()).decode('utf-8')

# Usage
base64_data = image_to_base64("Silver.png")
print(base64_data)