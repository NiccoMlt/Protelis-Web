import React from 'react';
import TreeView, { TreeViewProps } from '@material-ui/lab/TreeView';
import {
  DescriptionRounded,
  FolderOpenRounded,
  FolderRounded,
} from '@material-ui/icons';
import { ProtelisFile } from '../../model/File';
import { FileTreeItem } from './FileTreeItem';

/** The FileTree view component gets contained files from props. */
type FileTreeViewProps = TreeViewProps & {
  /** The files to show */
  files: ProtelisFile[];
};

/**
 * React Function Component that draws a TreeView for a file structure.
 * @param props - includes the files to show and the standard TreeView props
 */
const FileTreeView: React.FC<FileTreeViewProps> = (props: FileTreeViewProps) => {
  const { files, ...treeViewProps } = props;

  /**
   * The function generates a TreeItem from files and folders.
   * @param file - the file(s) to generate items from
   * @param nodeId - the id of the node to draw
   *
   * @returns the TreeItem(s)
   */
  function fileToItem(
    file: ProtelisFile,
    basePath = '',
    nodeId = 0,
  ): JSX.Element {
    const newBasePath = `${basePath}/${file.name}`;
    if (Array.isArray(file.content)) {
      let nid: number = nodeId;
      return (
        <FileTreeItem
          filePath={newBasePath}
          nodeId={`${nodeId}`}
          label={file.name}
          key={`${file.name}-${nodeId}`}
        >
          {
            file.content
              .map((f: ProtelisFile) => {
                const jsx = fileToItem(f, newBasePath, nid);
                nid += 1;
                return jsx;
              })
          }
        </FileTreeItem>
      );
    }
    return (
      <FileTreeItem
        filePath={newBasePath}
        nodeId={`${nodeId}`}
        label={file.name}
        key={`${newBasePath}-${nodeId}`}
      />
    );
  }

  return (
    <TreeView
      defaultEndIcon={<DescriptionRounded />}
      defaultCollapseIcon={<FolderOpenRounded />}
      defaultExpandIcon={<FolderRounded />}
      {...treeViewProps}
    >
      {Array.from(files).map((f) => fileToItem(f))}
    </TreeView>
  );
};

export default FileTreeView;
