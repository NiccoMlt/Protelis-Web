import React from 'react';
import TreeView from '@material-ui/lab/TreeView';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import { TreeItem } from '@material-ui/lab';
import { ProtelisFile } from '../model/File';

/** The FileTree view component gets contained files from props. */
type FileTreeViewProps = {
  /** The files to show */
  files: Array<ProtelisFile>
};

/**
 * The function generates a TreeItem from files and folders.
 * @param file the file(s) to generate items from
 * @param nodeId the id of the node to draw
 *
 * @return the TreeItem(s)
 */
function fileToItem(
  file: ProtelisFile,
  nodeId: number = 1,
): JSX.Element {
  if (Array.isArray(file.content)) {
    let nid: number = nodeId;
    return (
      <TreeItem nodeId={`${nodeId}`} label={file.name}>
        {
          file
            .content
            .map((f: ProtelisFile) => {
              const jsx = fileToItem(f, nid);
              nid += 1;
              return jsx;
            })
        }
      </TreeItem>
    );
  }
  return <TreeItem nodeId={`${nodeId}`} label={file.name} />;
}

/**
 * React Function Component that draws a TreeView for a file structure.
 * @param props the files to show
 */
const FileTreeView: React.FC<FileTreeViewProps> = (props: FileTreeViewProps) => {
  const { files } = props;
  return (
    <TreeView
      defaultCollapseIcon={<ExpandMoreIcon />}
      defaultExpandIcon={<ChevronRightIcon />}
    >
      {files.map((f) => fileToItem(f))}
    </TreeView>
  );
};

export default FileTreeView;
