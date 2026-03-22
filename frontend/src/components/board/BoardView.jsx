import { useEffect, useState } from "react";
import { DragDropContext, Droppable } from "@hello-pangea/dnd";
import ColumnManager from "./ColumnManager";
import { authApis } from "../../configs/Apis";
import MySpinner from "../layout/MySpinner";

const BoardView = ({ projectId }) => {
  const [columns, setColumns] = useState([]);
  const [loading, setLoading] = useState(false);

  const loadColumns = async () => {
    try {
      setLoading(true);
      const res = await authApis().get(`/projects/${projectId}/columns`);
      setColumns(res.data.result);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadColumns();
  }, []);

  const onDragEnd = async (result) => {
    const { destination, source, draggableId, type } = result;

    if (!destination) return;
    if (
      destination.droppableId === source.droppableId &&
      destination.index === source.index
    )
      return;

    try {
      if (type === "COLUMN") {
        const col = columns[source.index];
        const updatedColumns = Array.from(columns);
        updatedColumns.splice(source.index, 1);
        updatedColumns.splice(destination.index, 0, col);

        setColumns(updatedColumns);

        await authApis().patch(`/projects/${projectId}/columns/${col.id}`, {
          position: destination.index,
        });
      } else if (type === "TASK") {
        const sourceCol = columns.find((c) => c.id === source.droppableId);
        const destCol = columns.find((c) => c.id === destination.droppableId);
        const task = sourceCol.tasks[source.index];

        const newSourceTasks = Array.from(sourceCol.tasks);
        newSourceTasks.splice(source.index, 1);
        sourceCol.tasks = newSourceTasks;

        const newDestTasks = Array.from(destCol.tasks);
        newDestTasks.splice(destination.index, 0, task);
        destCol.tasks = newDestTasks;

        setColumns([...columns]);

        await authApis().put(`/tasks/${task.id}/move`, {
          destinationColumnId: destination.droppableId,
          newPosition: destination.index,
        });
      }
    } catch (err) {
      console.error(err);
    }
  };

  if (loading) return <MySpinner />;

  return (
    <DragDropContext onDragEnd={onDragEnd}>
      <Droppable droppableId="board" direction="horizontal" type="COLUMN">
        {(provided) => (
          <div
            style={{ display: "flex", gap: "10px", overflowX: "auto" }}
            ref={provided.innerRef}
            {...provided.droppableProps}
          >
            {columns.map((col, index) => (
              <ColumnManager
                key={col.id}
                column={col}
                index={index}
                reloadColumns={loadColumns}
              />
            ))}
            {provided.placeholder}
          </div>
        )}
      </Droppable>
    </DragDropContext>
  );
};

export default BoardView;
