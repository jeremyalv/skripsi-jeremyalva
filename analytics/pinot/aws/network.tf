resource "aws_vpc" "pinot_vpc" {
    cidr_block = var.base_cidr_block
    enable_dns_support = true
    enable_dns_hostnames = true
    
    tags = {
        Name = "Pinot-VPC"
    }
}

resource "aws_subnet" "default" {
    vpc_id = aws_vpc.pinot_vpc.id
    cidr_block = cidrsubnet(var.base_cidr_block, 8, 2)
    availability_zone = var.availability_zone
    map_public_ip_on_launch = true

    tags = {
        Name = "Pinot-Subnet"
    }
}

resource "aws_internet_gateway" "default" {
    vpc_id = aws_vpc.pinot_vpc.id

    tags = {
        Name = "Pinot-Internet-Gateway"
    }
}

resource "aws_default_route_table" "main" {
    default_route_table_id = aws_vpc.pinot_vpc.default_route_table_id

    route {
        cidr_block = "0.0.0.0/0"
        gateway_id = aws_internet_gateway.default.id
    }

    tags = {
        Name = "Pinot-Main-RT"
    }
}
