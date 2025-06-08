resource "aws_security_group" "default" {
    name = "pinot-terraform"
    vpc_id = aws_vpc.pinot_vpc.id

    # Allow SSH from anywhere
    ingress {
        from_port = 22
        to_port = 22
        protocol = "tcp"
        cidr_blocks = ["0.0.0.0/0"]
    }

    # All ports open within the VPC
    ingress {
        from_port = 0
        to_port = 65535
        protocol = "tcp"
        cidr_blocks = ["0.0.0.0/0"]
    }

    # Outbound internet access to anywhere
    egress {
        from_port = 0
        to_port = 0
        protocol = "-1"
        cidr_blocks = ["0.0.0.0/0"]
    }

    tags = {
        Name = "Pinot-Security-Group"
    }
}
