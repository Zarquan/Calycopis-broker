#!/bin/python
# <meta:header>
#   <meta:licence>
#     Copyright (c) 2025, Manchester (http://www.manchester.ac.uk/)
#
#     This information is free software: you can redistribute it and/or modify
#     it under the terms of the GNU General Public License as published by
#     the Free Software Foundation, either version 3 of the License, or
#     (at your option) any later version.
#
#     This information is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU General Public License for more details.
#
#     You should have received a copy of the GNU General Public License
#     along with this program.  If not, see <http://www.gnu.org/licenses/>.
#   </meta:licence>
# </meta:header>
#
#zrq-notes-indent
#
# AIMetrics: [
#     {
#     "name": "ChatGPT",
#     "contribution": "100%",
#     "references": [
#       "https://chatgpt.com/share/6854571e-1b5c-8008-986a-ba74db5d491f"
#       ]
#     }
#   ]
#

import yaml
import os
import argparse

def load_yaml(file_path):
    with open(file_path, 'r') as f:
        return yaml.safe_load(f)

def normalize_ref(ref):
    return ref.split("/")[-1]

def resolve_ref_target(ref, base_path):
    if '#' not in ref:
        return None, None, None

    file_part, fragment = ref.split('#', 1)
    path = os.path.normpath(os.path.join(base_path, file_part)) if file_part else None
    new_base = os.path.dirname(path) if path else base_path
    spec = load_yaml(path) if path else None

    if spec is None:
        return None, None, None

    value = spec
    for part in fragment.strip("/").split("/"):
        if not isinstance(value, dict):
            return None, None, None
        value = value.get(part)
        if value is None:
            return None, None, None
    return value, new_base, spec

def process_refs(spec, base_path, main_local_schemas=None, inlined=None, external_specs=None):
    if inlined is None:
        inlined = set()
    if external_specs is None:
        external_specs = {}

    main_local_schemas = spec.setdefault("components", {}).setdefault("schemas", {})

    def recurse(data, current_base_path, current_spec_schemas):
        if isinstance(data, dict):
            for key, value in list(data.items()):
                if key == "$ref" and isinstance(value, str):
                    # Expand short-form
                    if "#" not in value and "/" not in value:
                        expanded = f"#/components/schemas/{value}"
                        print(f"Expanding short $ref: '{value}' → '{expanded}'")
                        data[key] = value = expanded

                    # External $ref
                    if ".yaml" in value and value.index(".yaml") < value.index("#"):
                        ref_key = (value, current_base_path)
                        if ref_key in inlined:
                            continue
                        inlined.add(ref_key)

                        component, new_base, ext_spec = resolve_ref_target(value, current_base_path)
                        if not component:
                            print(f"Failed to resolve: {value}")
                            continue

                        schema_name = normalize_ref(value)
                        if schema_name not in main_local_schemas:
                            print(f"Inlining schema '{schema_name}' from: {value}")
                            main_local_schemas[schema_name] = component

                            ext_schemas = ext_spec.get("components", {}).get("schemas", {})
                            external_specs[new_base] = ext_schemas

                            recurse(component, new_base, ext_schemas)

                        data[key] = f"#/components/schemas/{schema_name}"

                    # Internal $ref
                    elif value.startswith("#/components/schemas/"):
                        schema_name = normalize_ref(value)
                        if schema_name in current_spec_schemas:
                            if schema_name not in main_local_schemas:
                                print(f"Inlining local schema from context: {schema_name}")
                                component = current_spec_schemas[schema_name]
                                main_local_schemas[schema_name] = component
                                recurse(component, current_base_path, current_spec_schemas)
                        else:
                            print(f"Warning: Could not find internal schema '{schema_name}' in current spec context")

                elif key == "discriminator" and isinstance(value, dict):
                    mapping = value.get("mapping")
                    if isinstance(mapping, dict):
                        for mkey, mval in list(mapping.items()):
                            if "#" not in mval:
                                new_ref = f"#/components/schemas/{mval}"
                                print(f"Expanding discriminator mapping: {mval} → {new_ref}")
                                mapping[mkey] = new_ref
                else:
                    recurse(value, current_base_path, current_spec_schemas)
        elif isinstance(data, list):
            for item in data:
                recurse(item, current_base_path, current_spec_schemas)

    current_spec_schemas = spec.get("components", {}).get("schemas", {})
    recurse(spec, base_path, current_spec_schemas)

def main():
    parser = argparse.ArgumentParser(description="Inline and expand OpenAPI $ref entries.")
    parser.add_argument("input", help="Input OpenAPI YAML file")
    parser.add_argument("output", help="Output YAML file with inlined references")
    args = parser.parse_args()

    input_path = os.path.abspath(args.input)
    output_path = os.path.abspath(args.output)
    base_path = os.path.dirname(input_path)

    print(f"Input:  {input_path}")
    print(f"Output: {output_path}")

    spec = load_yaml(input_path)
    process_refs(spec, base_path)

    with open(output_path, "w") as f:
        yaml.dump(spec, f)

    print("All references processed and file saved.")

if __name__ == "__main__":
    main()


