import os
from pathlib import Path

import grpc_tools.protoc

from utils.constants import PROJECT_ROOT_PATH


def compile_protobuf_python_code(proto_path: Path):
    """
    Compile Protobuf Python code.
    Args:
        proto_path: Path contains protobuf files.
    """
    protobuf_file_paths = list(proto_path.glob('**/*.proto'))

    compile_protobuf_paths = list()

    for protobuf_file_path in protobuf_file_paths:
        stem = protobuf_file_path.stem
        pb2_path = protobuf_file_path.with_name(f'{stem}_pb2.py')
        pb2_grpc_path = protobuf_file_path.with_name(f'{stem}_pb2_grpc.py')
        if any([not pb2_path.exists(), not pb2_grpc_path.exists()]):
            compile_protobuf_paths.append(str(protobuf_file_path.relative_to(PROJECT_ROOT_PATH)))

    if len(compile_protobuf_paths) > 0:
        command_arguments = [
            f'-I.',
            f'--python_out=.',
            f'--grpc_python_out=.',
            *compile_protobuf_paths
        ]
        cwd = os.getcwd()
        os.chdir(PROJECT_ROOT_PATH)
        print('Found uncompiled protobuf files, we are compiling the below files for you:')
        print(compile_protobuf_paths)
        print(
            'Do note that if you have make changes to the above protobuf files, you should manually DELETE the compiled'
            'python files (`*_pb.py`, `_pb_grpc.py`) in order that we can auto recompile for you.')
        print('Otherwise, your protobuf may work improperly.')
        grpc_tools.protoc.main(command_arguments)
        os.chdir(cwd)


PACKAGE_PATH = Path(__file__).parent.absolute()

compile_protobuf_python_code(PACKAGE_PATH)
