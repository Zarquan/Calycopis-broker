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

def get_schema_name_from_fragment(fragment):
    return fragment.strip().split("/")[-1]

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

def process_refs(spec, base_path, root_file, inlined=None, external_specs=None, ref_remap=None):
    if inlined is None:
        inlined = set()
    if external_specs is None:
        external_specs = {}
    if ref_remap is None:
        ref_remap = {}

    main_local_schemas = spec.setdefault("components", {}).setdefault("schemas", {})
    root_spec_schemas = spec.get("components", {}).get("schemas", {})

    def recurse(data, current_base_path, current_spec_schemas, root_spec_schemas):
        if isinstance(data, dict):
            for key, value in list(data.items()):
                if key == "$ref" and isinstance(value, str):
                    if "#" not in value and "/" not in value:
                        expanded = f"#/components/schemas/{value}"
                        print(f"Expanding short $ref: '{value}' → '{expanded}'")
                        data[key] = value = expanded

                    if ".yaml" in value and "#" in value:
                        file_part, fragment = value.split('#', 1)
                        abs_path = os.path.abspath(os.path.join(current_base_path, file_part))
                        schema_name = get_schema_name_from_fragment(fragment)
                        internal_ref = f"#/components/schemas/{schema_name}"
                        ref_key = (abs_path, fragment)

                        if abs_path == root_file:
                            if schema_name not in main_local_schemas:
                                print(f"Inlining back-reference to main spec: {schema_name}")
                                component = root_spec_schemas.get(schema_name)
                                if component:
                                    main_local_schemas[schema_name] = component
                                    recurse(component, base_path, root_spec_schemas, root_spec_schemas)
                            ref_remap[ref_key] = internal_ref
                            data[key] = internal_ref
                            continue

                        if ref_key in ref_remap:
                            data[key] = ref_remap[ref_key]
                            continue

                        if ref_key in inlined:
                            continue
                        inlined.add(ref_key)

                        component, new_base, ext_spec = resolve_ref_target(value, current_base_path)
                        if not component:
                            print(f"⚠️ Could not resolve external $ref: {value}")
                            continue

                        if schema_name not in main_local_schemas:
                            print(f"Inlining external schema '{schema_name}' from: {value}")
                            main_local_schemas[schema_name] = component
                            ext_schemas = ext_spec.get("components", {}).get("schemas", {})
                            external_specs[new_base] = ext_schemas
                            recurse(component, new_base, ext_schemas, root_spec_schemas)

                        ref_remap[ref_key] = internal_ref
                        data[key] = internal_ref

                    elif value.startswith("#/components/schemas/"):
                        schema_name = get_schema_name_from_fragment(value)

                        if schema_name in current_spec_schemas:
                            if schema_name not in main_local_schemas:
                                print(f"Inlining local schema: {schema_name}")
                                component = current_spec_schemas[schema_name]
                                main_local_schemas[schema_name] = component
                                recurse(component, current_base_path, current_spec_schemas, root_spec_schemas)

                        elif schema_name in root_spec_schemas:
                            if schema_name not in main_local_schemas:
                                print(f"Inlining schema from root spec: {schema_name}")
                                component = root_spec_schemas[schema_name]
                                main_local_schemas[schema_name] = component
                                recurse(component, base_path, root_spec_schemas, root_spec_schemas)

                        else:
                            print(f"⚠️ Could not find internal schema '{schema_name}' in any context")

                elif key == "discriminator" and isinstance(value, dict):
                    mapping = value.get("mapping")
                    if isinstance(mapping, dict):
                        for mkey, mval in list(mapping.items()):
                            if isinstance(mval, str):
                                if mval.startswith("#/components/schemas/"):
                                    schema_name = get_schema_name_from_fragment(mval)
                                    if schema_name in current_spec_schemas and schema_name not in main_local_schemas:
                                        print(f"Inlining from discriminator mapping: {schema_name}")
                                        main_local_schemas[schema_name] = current_spec_schemas[schema_name]
                                        recurse(current_spec_schemas[schema_name], current_base_path, current_spec_schemas, root_spec_schemas)
                                    elif schema_name in root_spec_schemas and schema_name not in main_local_schemas:
                                        print(f"Inlining from root via discriminator mapping: {schema_name}")
                                        main_local_schemas[schema_name] = root_spec_schemas[schema_name]
                                        recurse(root_spec_schemas[schema_name], base_path, root_spec_schemas, root_spec_schemas)

                                elif ".yaml" in mval and "#" in mval:
                                    file_part, fragment = mval.split('#', 1)
                                    abs_path = os.path.abspath(os.path.join(current_base_path, file_part))
                                    schema_name = get_schema_name_from_fragment(fragment)
                                    internal_ref = f"#/components/schemas/{schema_name}"
                                    ref_key = (abs_path, fragment)

                                    if abs_path == root_file:
                                        if schema_name not in main_local_schemas:
                                            print(f"Inlining back-reference from discriminator: {schema_name}")
                                            component = root_spec_schemas.get(schema_name)
                                            if component:
                                                main_local_schemas[schema_name] = component
                                                recurse(component, base_path, root_spec_schemas, root_spec_schemas)
                                        ref_remap[ref_key] = internal_ref
                                        mapping[mkey] = internal_ref
                                        continue

                                    if ref_key in ref_remap:
                                        mapping[mkey] = ref_remap[ref_key]
                                        continue

                                    if ref_key in inlined:
                                        continue
                                    inlined.add(ref_key)

                                    component, new_base, ext_spec = resolve_ref_target(mval, current_base_path)
                                    if not component:
                                        print(f"⚠️ Could not resolve external discriminator mapping: {mval}")
                                        continue

                                    if schema_name not in main_local_schemas:
                                        print(f"Inlining external schema from discriminator mapping: {schema_name}")
                                        main_local_schemas[schema_name] = component
                                        ext_schemas = ext_spec.get("components", {}).get("schemas", {})
                                        external_specs[new_base] = ext_schemas
                                        recurse(component, new_base, ext_schemas, root_spec_schemas)

                                    ref_remap[ref_key] = internal_ref
                                    mapping[mkey] = internal_ref

                else:
                    recurse(value, current_base_path, current_spec_schemas, root_spec_schemas)

        elif isinstance(data, list):
            for item in data:
                recurse(item, current_base_path, current_spec_schemas, root_spec_schemas)

    recurse(spec, base_path, root_spec_schemas, root_spec_schemas)

def main():
    parser = argparse.ArgumentParser(description="Inline and expand OpenAPI $ref entries.")
    parser.add_argument("input", help="Input OpenAPI YAML file")
    parser.add_argument("output", help="Output YAML file with inlined references")
    args = parser.parse_args()

    input_path = os.path.abspath(args.input)
    output_path = os.path.abspath(args.output)
    base_path = os.path.dirname(input_path)
    root_file = input_path

    print(f"📥 Input:  {input_path}")
    print(f"📤 Output: {output_path}")

    spec = load_yaml(input_path)
    process_refs(spec, base_path, root_file)

    with open(output_path, "w") as f:
        yaml.dump(spec, f, sort_keys=False)

    print("✅ All references processed and saved.")

if __name__ == "__main__":
    main()


