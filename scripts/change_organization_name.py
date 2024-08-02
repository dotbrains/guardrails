import sys
import re
import glob

# Get new organization name from command line
new_organization_name = sys.argv[1]

# Make sure that the new organization name is not empty
if not new_organization_name:
    raise Exception('New organization name cannot be empty!')

# Make sure the new organization name does not contain spaces
if ' ' in new_organization_name:
    raise Exception('New organization name cannot contain spaces!')

# Make sure the new organization name does not contain special characters or numbers

regex = re.compile('[^a-zA-Z_-]')

if regex.search(new_organization_name):
    raise Exception('New organization name can only contain letters!')

# Obtain list of all files in the repository that contain {ORGANIZATION}
files = glob.glob('**/*', recursive=True)
files = [file for file in files if '{ORGANIZATION}' in open(file).read()]

# Iterate over all files
for file in files:
    # Read the file
    with open(file, 'r') as f:
        content = f.read()

    # Replace old organization name with new organization name
    content = content.replace('{ORGANIZATION}', new_organization_name)

    # Write the file
    with open(file, 'w') as f:
        f.write(content)
