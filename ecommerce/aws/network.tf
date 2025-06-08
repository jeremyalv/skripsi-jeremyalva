resource "aws_vpc" "ecommerce_vpc" {
    cidr_block = var.base_cidr_block
    enable_dns_support = true
    enable_dns_hostnames = true
    
    tags = {
        Name = "Ecommerce-VPC"
    }
}

resource "aws_subnet" "default" {
    vpc_id = aws_vpc.ecommerce_vpc.id
    cidr_block = cidrsubnet(var.base_cidr_block, 8, 2)
    availability_zone = var.availability_zone
    map_public_ip_on_launch = true

    tags = {
        Name = "Ecommerce-Subnet"
    }
}

resource "aws_subnet" "failover" {
    vpc_id = aws_vpc.ecommerce_vpc.id
    cidr_block = cidrsubnet(var.base_cidr_block, 8, 3)
    availability_zone = var.availability_zone_failover
    map_public_ip_on_launch = true

    tags = {
        Name = "Ecommerce-Subnet-Failover"
    }
}

resource "aws_internet_gateway" "default" {
    vpc_id = aws_vpc.ecommerce_vpc.id

    tags = {
        Name = "Ecommerce-Internet-Gateway"
    }
}

resource "aws_default_route_table" "main" {
    default_route_table_id = aws_vpc.ecommerce_vpc.default_route_table_id

    route {
        cidr_block = "0.0.0.0/0"
        gateway_id = aws_internet_gateway.default.id
    }

    tags = {
        Name = "Ecommerce-Main-RT"
    }
}

resource "aws_db_subnet_group" "default" {
    name = "rds-subnet"
    subnet_ids = [ aws_subnet.default.id, aws_subnet.failover.id ]

    tags = {
        Name = "Ecommerce-Subnet-Group"
    }
}
