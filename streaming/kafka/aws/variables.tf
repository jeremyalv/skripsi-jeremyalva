variable "aws_access_key" {
    type = string
    description = "AWS Access Key ID"
}

variable "aws_secret_key" {
    type = string
    description = "AWS Secret Access Key"
}

variable "s3_bucket_name" {
    type = string
    default = "S3 Bucket Name for storing binaries"
}

variable "public_key_path" {
    type = string
    description = <<DESCRIPTION
        Path to the SSH public key to be used for authentication.
        Ensure this keypair is added to your local SSH agent so provisioners can
        connect.

        Example: ~/.ssh/my_keys.pub
        Default: ~/.ssh/id_rsa.pub
    DESCRIPTION
}

variable "key_name_prefix" {
    type = string
    description = "The prefix for the randomly generated name for the AWS key pair to be used for SSH connections (e.g. `kafka-terraform-ssh-keys-0a1b2cd3`)"
    default = "kafka-terraform-ssh-keys"
}

variable "region" {
    type = string
    description = "The AWS region in which the cluster will be deployed"
}

variable "availability_zone" {
    type = string
    description = "The AWS availability zone in which the cluster will run"
}

variable "aws_ami" {
    type = string
    description = "The AWS AMI to be used by the cluster instances"
}

variable "num_zookeeper_nodes" {
    type = number
    description = "The number of EC2 instances running ZooKeeper"
}

variable "num_broker_nodes" {
    type = number
    description = "The number of EC2 instances running Kafka brokers"
}

variable "instance_types" {
    type = map(string)
    description = "Key value pair of cluster components and their EC2 types"
}

variable "base_cidr_block" {
    type = string
    description = "The baseline CIDR block to be used by network assets for the cluster"
}

variable "broker_allocated_storage" {
    type = string
    description = "GBs of allocated EBS storage for broker"
}

variable "volume_device" {
    type = string
    description = "Volume to attach EBS disks"
}
