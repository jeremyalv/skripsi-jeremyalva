resource "aws_security_group" "default" {
    name = "pulsar-terraform"
    vpc_id = aws_vpc.pulsar_vpc.id

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
        Name = "Pulsar-Security-Group"
    }
}

# IAM Roles
resource "aws_iam_role" "ec2_s3_access_role" {
    name = "${var.key_name_prefix}-ec2-s3-role"
    assume_role_policy = jsonencode({
        Version = "2012-10-17"
        Statement = [
            {
                Action = "sts:AssumeRole"
                Effect = "Allow"
                Principal = {
                    Service = "ec2.amazonaws.com"
                }
            },
        ]
    })

    tags = {
        Name = "EC2-S3-Access-Role"
    }
}
resource "aws_iam_policy" "ec2_s3_access_policy" {
    name        = "${var.key_name_prefix}-ec2-s3-policy"
    description = "Policy for EC2 instances to access S3 bucket for Pulsar binary download"
    policy = jsonencode({
        Version = "2012-10-17"
        Statement = [
            {
                Action = [
                    "s3:GetObject",
                    "s3:ListBucket"
                ]
                Effect   = "Allow"
                Resource = [
                    "arn:aws:s3:::${var.s3_bucket_name}",
                    "arn:aws:s3:::${var.s3_bucket_name}/*",
                ]
            },
        ]
    })

    tags = {
        Name = "EC2-S3-Access-Policy"
    }
}

resource "aws_iam_policy_attachment" "ec2_s3_policy_attachment" {
    name       = "${var.key_name_prefix}-ec2-s3-policy-attach"
    roles      = [aws_iam_role.ec2_s3_access_role.name]
    policy_arn = aws_iam_policy.ec2_s3_access_policy.arn
}

resource "aws_iam_instance_profile" "ec2_instance_profile" {
    name = "${var.key_name_prefix}-ec2-instance-profile"
    role = aws_iam_role.ec2_s3_access_role.name

    tags = {
        Name = "EC2-Instance-Profile"
    }
}
