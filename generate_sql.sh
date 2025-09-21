#!/bin/bash
#make dir if not exists
mkdir -p "docker/mysql/init"

#load .env file
source .env
#generate file with values on .env
cat > docker/mysql/init/permissions.sql <<EOF
GRANT TRIGGER \´${DB_NAME}\´.* TO '${DB_USER}'@'%;
FLUSH PRIVILEGES;
EOF

echo file permissions.sql generated.