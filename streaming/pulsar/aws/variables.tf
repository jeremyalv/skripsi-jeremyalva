variable "aws_access_key" {
    description = "AWS Access Key ID"
}

variable "aws_secret_key" {
    description = "AWS Secret Access Key"
}

variable "s3_bucket_name" {
    default = "S3 Bucket Name for storing binaries"
}

variable "public_key_path" {
    description = <<DESCRIPTION
        Path to the SSH public key to be used for authentication.
        Ensure this keypair is added to your local SSH agent so provisioners can
        connect.

        Example: ~/.ssh/my_keys.pub
        Default: ~/.ssh/id_rsa.pub
    DESCRIPTION
}

variable "key_name_prefix" {
    description = "The prefix for the randomly generated name for the AWS key pair to be used for SSH connections (e.g. `pulsar-terraform-ssh-keys-0a1b2cd3`)"
    default = "pulsar-terraform-ssh-keys"
}

variable "region" {
    description = "The AWS region in which the Pulsar cluster will be deployed"
}

variable "availability_zone" {
    description = "The AWS availability zone in which the cluster will run"
}

variable "aws_ami" {
    description = "The AWS AMI to be used by the Pulsar cluster"
}

variable "num_zookeeper_nodes" {
    description = "The number of EC2 instances running ZooKeeper"
}

variable "num_bookie_nodes" {
    description = "The number of EC2 instances running BookKeeper"
}

variable "num_broker_nodes" {
    description = "The number of EC2 instances running Pulsar brokers"
}

variable "instance_types" {
    type = map(string)
}

variable "base_cidr_block" {
    description = "The baseline CIDR block to be used by network assets for the Pulsar cluster"
}

variable "broker_allocated_storage" {
    type = string
    description = "GBs of allocated EBS storage for broker"
}

variable "volume_device" {
    type = string
    description = "Volume to attach EBS disks"
}
